package com.vsa.ecommerce.common.util;

import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    private FileUtil() {}

    public static String getExtension(String filename) {
        return FilenameUtils.getExtension(filename);
    }

    public static String getBaseName(String filename) {
        return FilenameUtils.getBaseName(filename);
    }

    public static Path resolvePath(String baseDir, String... parts) {
        return Paths.get(baseDir, parts);
    }
}
