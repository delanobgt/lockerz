package com.delanobgt.lockerz.modules;

import com.delanobgt.lockerz.room.entities.FileItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class FileWorm {

    public static List<File> convertFileItemsToFiles(List<FileItem> fileItems) {
        List<File> resultFiles = new ArrayList<>();
        for (FileItem fileItem : fileItems) {
            resultFiles.add(fileItem.getFile());
        }
        return resultFiles;
    }

    public static List<File> flattenFiles(List<File> files) {
        List<File> resultFiles = new ArrayList<>();
        for (File file : files) {
            resultFiles.addAll(flattenFile(file));
        }
        return resultFiles;
    }

    public static List<File> flattenFile(File file) {
        if (file.isFile()) {
            return new ArrayList<>(Arrays.asList(file));
        } else {
            List<File> files = new ArrayList<>();
            File[] childFiles = file.listFiles();
            for (File childFile : childFiles) {
                files.addAll(flattenFile(childFile));
            }
            return files;
        }
    }

    public static List<FileGroup> divideIntoFileGroups(List<File> files, int n) {
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File a, File b) {
                return -(a.length() == b.length() ? 0 : a.length() < b.length() ? -1 : 1);
            }
        });
        PriorityQueue<FileGroup> pq = new PriorityQueue<>(Math.max(1, n), new Comparator<FileGroup>() {
            @Override
            public int compare(FileGroup a, FileGroup b) {
                return (a.getTotalBytes() == b.getTotalBytes() ? 0 : a.getTotalBytes() < b.getTotalBytes() ? -1 : 1);
            }
        });
        for (int i = 0; i < n; i++) {
            pq.add(new FileGroup());
        }
        for (File file : files) {
            FileGroup fg = pq.poll();
            fg.addFile(file);
            pq.offer(fg);
        }
        return new ArrayList<>(pq);
    }

    public static File addPostfixFileExtension(File f, String newExtension) {
        return new File(f.getAbsolutePath() + newExtension);
    }

    public static File removePostfixFileExtension(File f, String newExtension) {
        if (f.getAbsolutePath().endsWith(".lockz")) {
            return new File(f.getAbsolutePath().substring(0, f.getAbsolutePath().length() - 6));
        }
        return f;
    }

    public static FileItem addPostfixFileItemExtension(FileItem f, String newExtension) {
        f.setPath(f.getFile().getAbsolutePath() + newExtension);
        return f;
    }

    public static FileItem removePostfixFileItemExtension(FileItem f, String newExtension) {
        if (f.getFile().getAbsolutePath().endsWith(".lockz")) {
            f.setPath(f.getFile().getAbsolutePath().substring(0, f.getFile().getAbsolutePath().length() - 6));
            return f;
        }
        return f;
    }
}
