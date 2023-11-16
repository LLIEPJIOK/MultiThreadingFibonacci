import java.io.*;
import java.util.Queue;

// class for thread that do tasks
public class CheckingThread extends Thread {
    // queue for tasks
    private final Queue<Task> resultQueue;

    // flag for control
    private boolean isWaiting = false;

    // print stream for writing
    private final PrintStream printStream;

    // for calling back when finish executing
    private final CallBack callBack;

    // constructor
    CheckingThread(Queue<Task> resultQueue, PrintStream printStream, CallBack callBack) {
        this.resultQueue = resultQueue;
        this.printStream = printStream;
        this.callBack = callBack;
    }

    // checking answers
    private void check() {
        Task curTask;
        while (true) {
            if (!resultQueue.isEmpty()) {
                curTask = resultQueue.remove();
                try {
                    BufferedReader standardReader = new BufferedReader(new FileReader(curTask.getPath() + ".OUT"));
                    BufferedReader resultReader = new BufferedReader(new FileReader(curTask.getPath() + ".ANS"));
                    String standard = standardReader.readLine().trim();
                    String result = resultReader.readLine().trim();

                    // if results are different
                    if (!standard.equals(result)) {
                        Writer writer = new FileWriter(curTask.getPath() + ".ANS");
                        writer.write(standard);
                        writer.close();
                        printStream.println("CheckingThread: Answer for " + PathConvertor.extractFileName(curTask.getPath()) + " isn't correct. " +
                                "Expected " + standard + ", but got " + result + ". File with result rewriting");
                    } else {
                        printStream.println("CheckingThread: Answer for " + PathConvertor.extractFileName(curTask.getPath()) + " is correct (" + result + ")");
                    }
                    standardReader.close();
                    resultReader.close();
                } catch (IOException e) {
                    String path = curTask.getPath();
                    printStream.println("CheckingThread: Can't read/write file for " + path.substring(path.lastIndexOf("\\") + 1));
                }
            } else if (isWaiting) {
                break;
            }
            // sleep for getting data
            try {
                sleep(1);
            } catch (InterruptedException e) {
                printStream.println("CheckingThread: Error with thread");
                break;
            }
        }
        callBack.notifyFinishing();
    }

    // for thread run
    @Override
    public void run() {
        check();
    }

    // setter
    public void waitThread() {
        this.isWaiting = true;
    }
}
