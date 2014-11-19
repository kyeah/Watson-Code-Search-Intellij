import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.InlineView;

/**
 * Created by kyeh on 11/18/14.
 */
public class HTMLWrappingEditorKit extends HTMLEditorKit {

    @Override
    public ViewFactory getViewFactory(){
        return new HTMLFactory(){

            public View create(Element e){
                View v = super.create(e);
                if(v instanceof InlineView){
                    return new InlineView(e){

                        boolean nowrap = false;

                        @Override
                        protected void setPropertiesFromAttributes() {
                            super.setPropertiesFromAttributes();

                            Object whitespace = this.getAttributes().getAttribute(CSS.Attribute.WHITE_SPACE);
                            if ((whitespace != null) && whitespace.equals("nowrap")) {
                                nowrap = true;
                            } else {
                                nowrap = false;
                            }
                        }

                        public int getBreakWeight(int axis, float pos, float len) {
                            if (nowrap) {
                                return BadBreakWeight;
                            }
                            return BadBreakWeight;
                            ///return super.getBreakWeight(axis, pos, len);
                        }
                    };
                }
                return v;
            }
        };
    }
}
