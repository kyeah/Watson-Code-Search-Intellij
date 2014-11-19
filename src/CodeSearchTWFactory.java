import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CodeSearchTWFactory implements ToolWindowFactory, KeyListener {

    private JTextPane textPane;
    private JPanel panel;
    private JTextField questionField;
    private JList answersList;
    private ToolWindow myToolWindow;
    private HTMLEditorKit kit;

    private Vector<String> answers = new Vector<String>();
    ExecutorService executor = Executors.newFixedThreadPool(1);

    public CodeSearchTWFactory() {
        questionField.addKeyListener(this);
    }

    // Create the tool window content.
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        myToolWindow = toolWindow;
        toolWindow.setTitle("Project Atlas");

        // TODO: Modify textPane Document to wrap text

        // add a HTMLEditorKit to the text pane
        kit = new HTMLEditorKit();
        textPane.setEditorKit(kit);

        // add some styles to the html
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("h1 {color: blue;}");
        styleSheet.addRule("h2 {color: #ff0000;}");
        styleSheet.addRule("p {color: #DEDEDE; font-size:13pt;}");
        styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");

        Document doc = kit.createDefaultDocument();
        textPane.setDocument(doc);
        textPane.setText("Enter a question to search Atlas documentation.");

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);

        answersList.setListData(answers);
        answersList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                selectAnswer(answersList.getSelectedIndex());
            }
        });
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            executor.submit(new WatsonRequest(this, questionField.getText()));
        }
    }

    public void setAnswers(Vector<String> newAnswers, Vector<String> shortAnswers) {
        answers = newAnswers;
        answersList.setListData(shortAnswers);
        selectAnswer(0);
    }

    private void selectAnswer(int index) {
        if (index < 0 || index >= answers.size()) {
            return;
        }

        textPane.setContentType("text/html");
        textPane.setText(answers.get(index));
    }
}