// class for converting paths
public class PathConvertor {
    // removing extension
    static String removeExtension(String path) {
        return path.substring(0, path.lastIndexOf("."));
    }

    // extracting fileName
    static String extractFileName(String path) {
        return path.substring(path.lastIndexOf("\\") + 1);
    }
}
