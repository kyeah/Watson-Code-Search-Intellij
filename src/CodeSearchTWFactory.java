import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey.Chursin
 * Date: Aug 25, 2010
 * Time: 2:09:00 PM
 */
public class CodeSearchTWFactory implements ToolWindowFactory, KeyListener {

    private JLabel title;
    private JTextPane text;
    private JPanel panel;
    private JTextField questionField;
    private ToolWindow myToolWindow;

    public CodeSearchTWFactory() {
        questionField.addKeyListener(this);
    }

    // Create the tool window content.
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        myToolWindow = toolWindow;
        text.setText("TESTING");
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            requestAnswerPost(questionField.getText());
        }
    }

    public boolean requestAnswerPost(String question) {
        final HttpClient httpclient = HttpClients.createDefault();
        final HttpPost httppost = new HttpPost("http://localhost:5000/submit");

        // Request parameters and other properties.
        final List<BasicNameValuePair> params = new ArrayList<>(2);
        params.add(new BasicNameValuePair("question", question));
        params.add(new BasicNameValuePair("json", "false"));

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

                    System.out.println(builder.toString());
                    text.setContentType("text/html");
                    text.setText(builder.toString());
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to get answer for question " + question);
            System.out.println(e.getMessage());
        }

        return false;
    }

}