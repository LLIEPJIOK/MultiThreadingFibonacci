import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;

// class for thread that checking input data
public class InputThread extends Thread {
    // path to data
    private final String directoryPath;

    // queue for tasks
    private final Queue<Task> taskQueue;

    // print stream for writing
    private final PrintStream printStream;

    // for calling back when finish executing
    private final CallBack callBack;

    // constructor
    InputThread(String directoryPath, Queue<Task> taskQueue, PrintStream printStream, CallBack callBack) {
        this.directoryPath = directoryPath;
        this.taskQueue = taskQueue;
        this.printStream = printStream;
        this.callBack = callBack;
    }

    // reading input
    private int readInteger(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = reader.readLine();
        int n = Integer.parseInt(line);
        reader.close();
        if (n < 1 || n > 10000000) {
            throw new NumberFormatException(line);
        }
        return n;
    }


    // scanning input
    private void scanInput() {
        try {
            Path dir = Paths.get(directoryPath);
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir, "*.IN");
            for (Path path : directoryStream) {
                // trying to read
                try {
                    String fileName = path.toAbsolutePath().toString();
                    int n = readInteger(fileName);
                    Task curTask = new Task(fileName, n);

                    // if there is no standard output file
                    if (!Files.exists(Paths.get(curTask.getPath() + ".OUT"))) {
                        printStream.println("InputThread: Standard output file not found for " + PathConvertor.removeExtension(path.getFileName().toString()));
                    }
                    else {
                        taskQueue.add(curTask);
                        printStream.println("InputThread: Reading input has finished for " + PathConvertor.removeExtension(path.getFileName().toString()) +
                                "; n = " + curTask.getN());
                    }
                } catch (FileNotFoundException e) {
                    printStream.println("InputThread: Input file not found for " + PathConvertor.removeExtension(path.getFileName().toString()));
                } catch (IOException e) {
                    printStream.println("InputThread: Can't read file for " + PathConvertor.removeExtension(path.getFileName().toString()));
                } catch (NumberFormatException e) {
                    printStream.println("InputThread: Can't do task, because required integer n (1 <= n <= 10000000), but got "
                            + e.getMessage() + " for " + PathConvertor.removeExtension(path.getFileName().toString()));
                }
            }
        } catch (IOException e) {
            printStream.println("InputThread: Error with current directory");
        }
        callBack.notifyFinishing();
    }

    // for thread run
    @Override
    public void run() {
        scanInput();
    }
}
