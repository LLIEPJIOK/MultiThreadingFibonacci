import javax.swing.*;

// main class
public class Main {

    // main function
    public static void main(String[] args) {
        // creating main window
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}