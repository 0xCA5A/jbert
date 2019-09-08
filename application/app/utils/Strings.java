package utils;

public class Strings {

    private Strings() {}

    public static String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9\\.\\- ]", "_");
    }
}
