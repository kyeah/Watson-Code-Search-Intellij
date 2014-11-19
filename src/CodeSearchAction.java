import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiElement;

/**
 * Created by kyeh on 11/12/14.
 */
public class CodeSearchAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        PsiElement element = e.getData(LangDataKeys.PSI_ELEMENT);
        System.out.println(element.toString());
    }
}
