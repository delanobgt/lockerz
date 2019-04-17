package com.delanobgt.lockerz.modules;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileGroup {

    private List<File> files = new ArrayList<>();
    private long totalBytes = 0;

    public FileGroup() {
    }

    public void addFile(File file) {
        files.add(file);
        totalBytes += file.length();
    }

    public void removeFileAt(int index) {
        totalBytes -= files.get(index).length();
        files.remove(index);
    }

    public List<File> getFiles() {
        return new ArrayList<>(files);
    }

    public int getFilesCount() {
        return files.size();
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("--FileGroup--").append('\n');
        sb.append("Total size (byte): ").append(totalBytes).append('\n');
        for (File file : files) {
            sb.append(file.getAbsolutePath()).append('\n');
        }
        sb.append('\n');
        return sb.toString();
    }

}