import javax.swing.*;
import java.io.OutputStream;

// output for textArea
public class TextAreaOutputStream extends OutputStream {
    // text area
    private final JTextArea textArea;

    // constructor
    public TextAreaOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    // writing data
    @Override
    public void write(int b) {
        synchronized (textArea) {
            textArea.append(String.valueOf((char) b));
        }
    }
}