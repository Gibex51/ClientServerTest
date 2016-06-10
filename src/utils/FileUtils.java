package utils;

import java.nio.file.*;

public final class FileUtils {
	public static String filterFileName(String fileName) {
		try {
			String r = fileName.replaceAll("[/\\\\]", "");
            Paths.get(r);
            return r;
        } catch (InvalidPathException |  NullPointerException ex) {
            return "";
        }
	}
}
