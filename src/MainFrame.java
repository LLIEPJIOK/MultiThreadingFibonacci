import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Queue;

public class MainFrame extends JFrame implements ActionListener {
    // staff for window
    private final JTextField pathField;
    private final JButton fileDialogBtn;
    private final JButton stopCalcBtn;
    private final JButton startCalcBtn;
    private final JTextArea textArea;

    // threads
    InputThread inputThread;
    CalculatingThread calculatingThread;
    CheckingThread checkingThread;

    // callback for threads to notify that they finish
    private final CallBack callBack;

    // number of finished threads
    private int numberOfFinishedThreads;

    // print stream for writing
    private final PrintStream printStream;

    MainFrame() {
        // configuring window
        this.setTitle("Thread application");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(200, 100, 650, 440);
        this.setIconImage(new ImageIcon("Icons\\MainWindowIcon.png").getImage());
        this.setResizable(false);

        // creating elements
        pathField = new JTextField(System.getProperty("user.dir"));
        pathField.setBounds(10, 10, 300, 30);
        pathField.setFont(new Font("Clarendon", Font.ITALIC, 12));
        pathField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false));
        pathField.setBackground(Color.WHITE);
        pathField.setEditable(false);

        fileDialogBtn = new JButton("...");
        fileDialogBtn.setBounds(320, 10, 50, 30);
        fileDialogBtn.setBackground(Color.WHITE);
        fileDialogBtn.setForeground(Color.BLACK);
        fileDialogBtn.setOpaque(true);
        fileDialogBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, false));
        fileDialogBtn.setFocusPainted(false);
        fileDialogBtn.setContentAreaFilled(true);
        fileDialogBtn.addActionListener(this);

        startCalcBtn = new JButton("Start calculating");
        startCalcBtn.setBounds(10, 50, 140, 30);
        startCalcBtn.setBackground(Color.WHITE);
        startCalcBtn.setForeground(Color.BLACK);
        startCalcBtn.setOpaque(true);
        startCalcBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, false));
        startCalcBtn.setFocusPainted(false);
        startCalcBtn.setContentAreaFilled(true);
        startCalcBtn.addActionListener(this);

        stopCalcBtn = new JButton("Stop calculating");
        stopCalcBtn.setBounds(10, 50, 140, 30);
        stopCalcBtn.setBackground(Color.WHITE);
        stopCalcBtn.setForeground(Color.BLACK);
        stopCalcBtn.setOpaque(true);
        stopCalcBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, false));
        stopCalcBtn.setFocusPainted(false);
        stopCalcBtn.setContentAreaFilled(true);
        stopCalcBtn.setVisible(false);
        stopCalcBtn.addActionListener(this);

        textArea = new JTextArea();
        textArea.setBounds(10, 90, 620, 300);
        textArea.setFont(new Font("Clarendon", Font.PLAIN, 13));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(10, 90, 618, 300);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false));

        // print stream for writing
        printStream = new PrintStream(new TextAreaOutputStream(textArea));

        // initializing thread staff
        numberOfFinishedThreads = 0;
        callBack = this::threadFinished;

        Panel panel = new Panel();
        panel.setBackground((new Color(133, 177, 222)));

        // adding elements
        this.add(pathField);
        this.add(fileDialogBtn);
        this.add(startCalcBtn);
        this.add(stopCalcBtn);
        this.add(scrollPane);
        this.add(panel);
    }

    // opening fileDialog
    private void openFileDialog() {
        JFileChooser fileChooser = new JFileChooser(pathField.getText());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showDialog(null, "Choose directory");

        // setting new path
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            pathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    // starting calculating
    private void startCalc() {
        // creating queues
        Queue<Task> taskQueue = new ArrayDeque<>();
        Queue<Task> resultQueue = new ArrayDeque<>();

        // creating threads
        inputThread = new InputThread(pathField.getText(), taskQueue, printStream, callBack);
        calculatingThread = new CalculatingThread(taskQueue, resultQueue, printStream, callBack);
        checkingThread = new CheckingThread(resultQueue, printStream, callBack);

        // starting threads
        printStream.println("Start calculating");
        inputThread.start();
        calculatingThread.start();
        checkingThread.start();
    }

    // handle thread's finishing
    private void threadFinished() {
        switch (++numberOfFinishedThreads) {
            case 1:
                calculatingThread.waitThread();
                break;
            case 2:
                checkingThread.waitThread();
                break;
            case 3:
                numberOfFinishedThreads = 0;
                printStream.println("Finish calculating");
                // returning to default buttons
                fileDialogBtn.setEnabled(true);
                startCalcBtn.setVisible(true);
                stopCalcBtn.setVisible(false);
                stopCalcBtn.setEnabled(true);
        }
    }

    // handling actions
    @Override
    public void actionPerformed(ActionEvent event) {
        // handling open file dialog
        if (event.getSource() == fileDialogBtn) {
            openFileDialog();
            return;
        }
        // handling start/stop calculating
        if (event.getSource() == startCalcBtn) {
            // prepare for tasks
            textArea.setText("");
            fileDialogBtn.setEnabled(false);
            startCalcBtn.setVisible(false);
            stopCalcBtn.setVisible(true);
            startCalc();
            return;
        }
        if (event.getSource() == stopCalcBtn) {
            stopCalcBtn.setEnabled(false);
            calculatingThread.stopThread();
            printStream.println("Calculating thread was stopped");
        }
    }
}
