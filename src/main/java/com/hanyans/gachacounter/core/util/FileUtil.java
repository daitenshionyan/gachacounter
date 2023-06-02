package com.hanyans.gachacounter.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;


/**
 * Utility class containing static methods to aid in file related operations.
 */
public class FileUtil {
    private static final int BUFFER_SIZE = 2097152; // 2MB


    /**
     * Creates the file and all required parent directories of the specified
     * path.
     *
     * @param path - the path of the file to create.
     * @return the {@code Path} to the created file.
     * @throws IOException if any I/O error occurs.
     */
    public static Path createFile(Path path) throws IOException {
        Objects.requireNonNull(path);
        if (Files.exists(path) && Files.isRegularFile(path)) {
            return path;
        }
        createParentDir(path);
        return Files.createFile(path);
    }


    /**
     * Creates the parent directory and all its uncreated parent directories of
     * the specified path.
     *
     * @param path - the path whose parent directories to create.
     * @return the {@code Path} to the parent directory of the specified path.
     *      Can be {@code null} which signifies that the given path has no
     *      parent directory.
     * @throws IOException if an I/O error occurs.
     */
    public static Path createParentDir(Path path) throws IOException {
        Objects.requireNonNull(path);
        Path parentDir = path.getParent();
        if (parentDir == null) {
            return null;
        }
        return Files.createDirectories(parentDir);
    }


    /**
     * Returns the {@code BufferedReader} of the file in the specified path.
     *
     * <p>The returned reader has a buffer size of 2MB.
     *
     * @param path - the path to the file whose {@code BufferedReader}
     *      to return.
     * @throws FileNotFoundException if the file cannot be found.
     */
    public static BufferedReader getFileReader(Path path) throws FileNotFoundException {
        Objects.requireNonNull(path);
        return new BufferedReader(new FileReader(path.toFile()), BUFFER_SIZE);
    }


    /**
     * Returns the {@code InputStream} to the file in the specified path.
     *
     * <p>The returned input stream has a buffer size of 2MB.
     *
     * @param path - the path to the file whose {@code InputStream} to return.
     * @throws FileNotFoundException if the file cannot be found.
     */
    public static BufferedInputStream getInputStream(Path path) throws FileNotFoundException {
        Objects.requireNonNull(path);
        return new BufferedInputStream(new FileInputStream(path.toFile()), BUFFER_SIZE);
    }


    /**
     * Returns the {@code BufferedWriter} to the file in the specified path.
     *
     * <p>The returned writer has a buffer size of 2MB.
     *
     * @param path - the path to the file whose {@code BufferedWriter} to
     *      return.
     * @throws IOException if an I/O error occurs.
     */
    public static BufferedWriter getFileWriter(Path path) throws IOException {
        Objects.requireNonNull(path);
        return new BufferedWriter(new FileWriter(path.toFile()), BUFFER_SIZE);
    }


    /**
     * Returns the URL of the specified path that is relative to the resource
     * directory.
     *
     * @param path - the path to the file relative to the resource directory
     *      whose URL to retrieve.
     * @throws FileNotFoundException if the file cannot be found.
     */
    public static URL getResourceUrl(String path) throws FileNotFoundException {
        URL url = FileUtil.class.getResource(path);
        if (url == null) {
            throw new FileNotFoundException(String.format("File cannot be found (%s)",
                    path.toString()));
        }
        return url;
    }


    /**
     * Returns the URI of the specified path that is relative to the resource
     * directory.
     *
     * @param pathString - the path String to the file relative to the resource
     *      directory whose URI to return.
     * @throws FileNotFoundException if the file cannot be found.
     */
    public static URI getResourceUri(String pathString)
                throws FileNotFoundException {
        try {
            return getResourceUrl(pathString).toURI();
        } catch (URISyntaxException uriSynEx) {
            throw new RuntimeException(uriSynEx);
        }
    }
}
