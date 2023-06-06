package com.hanyans.gachacounter.logic.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hanyans.gachacounter.core.PopupMessage;
import com.hanyans.gachacounter.core.task.RunnableTask;
import com.hanyans.gachacounter.core.util.FileUtil;
import com.hanyans.gachacounter.storage.StorageManager;

public class UpdateDataTask extends RunnableTask<PopupMessage> {
    private static final String DATA_ZIP_URL = "https://github.com/daitenshionyan/gachacounter/archive/refs/heads/data.zip";

    private static final Path ZIP_ROOT = Path.of("gachacounter-data");
    private static final HashSet<Path> IGNORE_PATH_SET = initializeIgnorePath();

    private final Logger logger = LogManager.getFormatterLogger(UpdateDataTask.class);


    private static HashSet<Path> initializeIgnorePath() {
        HashSet<Path> set = new HashSet<>();
        set.add(Path.of(""));
        set.add(Path.of(".gitignore"));
        return set;
    }


    @Override
    public PopupMessage performTask() throws Throwable {
        setProgress(-1D);
        long startTime = System.currentTimeMillis();
        URL url = new URL(DATA_ZIP_URL);
        ArrayList<Path> updatedList = new ArrayList<>();
        ZipInputStream zis = new ZipInputStream(url.openStream());
        ZipEntry entry = zis.getNextEntry();
        while (entry != null) {
            Path path = ZIP_ROOT.relativize(Path.of(entry.getName()));
            if (!IGNORE_PATH_SET.contains(path) && !entry.isDirectory()) {
                logger.trace("Processing zip entry -- \"%s\"", entry.getName());
                setMessage(path.toString());
                if (writeContent(zis, entry, path)) {
                    updatedList.add(path);
                }
            } else {
                logger.trace("Ignoring zip entry -- \"%s\"", entry.getName());
            }
            entry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Completed updating data files in %d ms", duration);
        return formMessage(updatedList);
    }


    private boolean writeContent(ZipInputStream zis, ZipEntry entry, Path path) throws IOException {
        File file = path.toFile();
        path.getRoot();
        if (file.exists() && !path.startsWith(StorageManager.EVENT_DIR_PATH)) {
            logger.trace("Zip entry already present, ignoring -- %s", entry.getName());
            return false;
        }
        FileUtil.createFile(path).toFile();
        try (FileOutputStream outStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = zis.read(buffer)) > 0) {
                outStream.write(buffer, 0, len);
            }
        }
        logger.info("Successfully downloaded data to \"%s\"", path);
        return true;
    }


    private PopupMessage formMessage(ArrayList<Path> updatedList) {
        if (updatedList.isEmpty()) {
            return new PopupMessage("Data files up to date", "Nothing updated", PopupMessage.MsgType.Success);
        }
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Updated %d data files:", updatedList.size()));
        for (Path path : updatedList) {
            builder.append("\n").append(path.toString());
        }
        return new PopupMessage("Data files updated", builder.toString(), PopupMessage.MsgType.Success);
    }
}
