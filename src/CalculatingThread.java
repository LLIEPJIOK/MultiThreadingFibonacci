import java.io.*;
import java.math.BigInteger;
import java.util.Queue;

// class for thread that do tasks
public class CalculatingThread extends Thread {
    // queue for tasks
    private final Queue<Task> taskQueue;
    private final Queue<Task> resultQueue;

    // first two fibonacci numbers
    private static BigInteger first;
    private static BigInteger second;

    // flags for control
    private boolean isStopped = false;
    private boolean isWaiting = false;

    // print stream for writing
    private final PrintStream printStream;

    // for calling back when finish executing
    private final CallBack callBack;

    // constructor
    CalculatingThread(Queue<Task> taskQueue, Queue<Task> resultQueue, PrintStream printStream, CallBack callBack) {
        this.taskQueue = taskQueue;
        this.resultQueue = resultQueue;
        this.printStream = printStream;
        this.callBack = callBack;
    }

    // finding n digit in sequence
    private int findNDigit(int n) {
        while (true) {
            String str = first.toString();

            // if digit in the string
            if (n <= str.length()) {
                return str.charAt(n - 1) - '0';
            }
            n -= str.length();

            // go to next two fibonacci numbers
            BigInteger tmp = new BigInteger(first.toString());
            first = new BigInteger(second.toString());
            second = second.add(tmp);
        }
    }

    // writing output
    private static void writeInteger(String fileName, int n) throws IOException {
        Writer writer = new FileWriter(fileName);
        writer.write(String.valueOf(n));
        writer.close();
    }

    // calculating
    private void calculate() {
        Task curTask;
        while (!isStopped) {
            if (!taskQueue.isEmpty()) {
                curTask = taskQueue.remove();
                first = new BigInteger("1");
                second = new BigInteger("1");
                try {
                    int ans = findNDigit(curTask.getN());
                    writeInteger(curTask.getPath() + ".ANS", ans);
                    resultQueue.add(curTask);
                    printStream.println("CalculatingThread: Calculating and writing answer in file have finished for " + PathConvertor.extractFileName(curTask.getPath()) +
                            "; ans = " + ans);
                } catch (IOException e) {
                    printStream.println("CalculatingThread: Can't write file for " + PathConvertor.extractFileName(curTask.getPath()));
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
        calculate();
    }

    public void stopThread() {
        this.isStopped = true;
    }

    public void waitThread() {
        this.isWaiting = true;
    }
}
