import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by kyeh on 11/18/14.
 */
public class WatsonRequest extends SwingWorker<WatsonRequest.Response, Void> {


    public class Response {
        public Response() {
        }
    }

    private String question;
    private CodeSearchTWFactory toolWindowFactory;

    private Vector<String> answers = new Vector<String>();
    private Vector<String> shortAnswers = new Vector<String>();

    public WatsonRequest(CodeSearchTWFactory toolWindowFactory, String question) {
        this.question = question;
        this.toolWindowFactory = toolWindowFactory;
    }


    @Override
    protected Response doInBackground() throws Exception {
        final HttpClient httpclient = HttpClients.createDefault();
        final HttpPost httppost = new HttpPost("http://localhost:5000/submit");

        // Request parameters and other properties.
        final List<BasicNameValuePair> params = new ArrayList<>(2);
        params.add(new BasicNameValuePair("question", question));
        params.add(new BasicNameValuePair("json", "true"));

        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            //Execute and return the response
            final HttpEntity entity = httpclient.execute(httppost).getEntity();

            if (entity != null) {
                try (InputStream instream = entity.getContent()) {
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
                    String line;
                    final StringBuilder builder = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                    JsonParser parser = new JsonParser();
                    JsonObject json = (JsonObject) parser.parse(builder.toString());
                    JsonArray results = json.getAsJsonArray("results");

                    for (int i = 0; i < results.size(); i++) {
                        JsonObject result = (JsonObject) results.get(i);
                        String answer = result.get("answer").toString();
                        answer = answer.replace("\\n", "");
                        answers.add(answer);

                        // TODO: Replace this with an actual safe html extractor
                        shortAnswers.add("[" + result.get("confidence").toString() + "%] " + answer.substring(0, 200).replaceAll("\\<[^>]*>", ""));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to get answer for question " + question);
            System.out.println(e.getMessage());
        }

        return new Response();
    }

    @Override
    protected void done() {
        toolWindowFactory.setAnswers(answers, shortAnswers);
    }
}
