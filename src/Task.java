// class for task
public class Task {
    // path to task
    private final String path;

    // fibonacci number
    private final int n;

    // constructor
    Task(String path, int n) {
        this.path = path.substring(0, path.lastIndexOf("."));
        this.n = n;
    }

    // getters
    public String getPath() {
        return path;
    }

    public int getN() {
        return n;
    }
}
