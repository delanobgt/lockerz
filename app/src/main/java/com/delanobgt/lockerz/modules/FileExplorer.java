package com.delanobgt.lockerz.modules;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;

public class FileExplorer {

    private static final String TAG = "FileExplorer";
    private static final String ROOT_PATH = Environment.getExternalStorageDirectory().toString();

    private File currentDir = new File(ROOT_PATH);
    private FileItem[] fileItemList;

    public FileExplorer() {
        loadFileList();
        Log.d(TAG, currentDir.getPath());
        for (FileItem fileItem : fileItemList) {
//            Log.d(TAG, fileItem.toString());
        }
    }

    public boolean navigateToFileItemIndex(int index) {
        if (0 <= index && index < fileItemList.length && fileItemList[index].getType() == FileType.DIRECTORY) {
            currentDir = new File(currentDir.getAbsolutePath() + "/" + fileItemList[index].getPath());
            loadFileList();
            return true;
        }
        return false;
    }

    public boolean navigateBack() {
        if (!currentDir.getAbsolutePath().equals(ROOT_PATH)) {
            int lastSlashIndex = currentDir.getAbsolutePath().lastIndexOf('/');
            String prevPath = currentDir.getAbsolutePath().substring(0, lastSlashIndex);
            currentDir = new File(prevPath);
            loadFileList();
            return true;
        }
        return false;
    }

    public boolean isOnRootDir() {
        return ROOT_PATH.equals(currentDir.getAbsolutePath());
    }

    public File getCurrentDir() {
        return currentDir;
    }

    public FileItem[] getFileItemList() {
        return fileItemList;
    }

    public FileItem getFileItemAt(int index) {
        if (0 <= index && index < fileItemList.length)
            return fileItemList[index];
        return null;
    }

    public void loadFileList() {
        try {
            currentDir.mkdirs();
        } catch (SecurityException e) {
            Log.e(TAG, "Unable to write on the SD Card!");
        }

        if (currentDir.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    File file = new File(dir, filename);
                    return (file.isFile() || file.isDirectory()) && !file.isHidden();
                }
            };

            String[] filteredFiles = currentDir.list(filter);
            fileItemList = new FileItem[filteredFiles.length];
            for (int i = 0; i < filteredFiles.length; i++) {
                File file = new File(currentDir, filteredFiles[i]);
                if (file.isDirectory()) {
                    fileItemList[i] = new FileItem(filteredFiles[i], FileType.DIRECTORY);
                } else {
                    fileItemList[i] = new FileItem(filteredFiles[i], FileType.FILE);
                }
            }
        } else {
            Log.e(TAG, "Path does not exist!");
        }
    }

    public enum FileType {
        FILE,
        DIRECTORY
    }

    public static class FileItem {
        private String path;
        private FileType type;
        private File file;

        public FileItem(String path, FileType type) {
            this.path = path;
            this.type = type;
            this.file = new File(path);
        }

        public String getPath() {
            return path;
        }

        public FileType getType() {
            return type;
        }

        public File getFile() {
            return file;
        }

        @Override
        public String toString() {
            return String.format(
                    "Path: %s\n" +
                            "Type: %s\n"
                    , path, type);
        }
    }

}