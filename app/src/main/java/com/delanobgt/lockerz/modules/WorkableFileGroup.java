package com.delanobgt.lockerz.modules;

import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;

import static com.delanobgt.lockerz.modules.FileWorm.addPostfixFileExtension;

public class WorkableFileGroup {

    private final long originalTotalBytes;
    private File[] originalFiles;
    private File[] modifiedFiles;
    private long modifiedTotalBytes = 0;
    private boolean cancelled = false;
    private boolean finished = false;
    private FileModifier fileModifier;
    private int fileIndex;
    private long seekPos, totalLen;
    private RandomAccessFile readRaf = null, writeRaf = null;
    private byte[] data = new byte[1048576];

    public WorkableFileGroup(FileGroup fileGroup, FileModifier fileModifier) {
        int filesCount = fileGroup.getFilesCount();
        this.fileModifier = fileModifier;
        originalFiles = fileGroup.getFiles().toArray(new File[filesCount]);
        modifiedFiles = new File[filesCount];
        originalTotalBytes = fileGroup.getTotalBytes();
        for (int i = 0; i < filesCount; i++) {
            try {
                File modifiedFile = addPostfixFileExtension(originalFiles[i], ".lockz");
                if (!modifiedFile.exists()) modifiedFile.createNewFile();
                modifiedFiles[i] = modifiedFile;
            } catch (Exception ex) {
                ex.printStackTrace();
                setCancelled();
            }
        }
    }

    public int getOriginalFilesCount() {
        return originalFiles.length;
    }

    public int getModifiedFilesCount() {
        return fileIndex;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished() {
        this.finished = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled() {
        this.cancelled = true;
    }

    public boolean hasNext() {
        return fileIndex < originalFiles.length;
    }

    public boolean travelNext() {
        try {
            if (seekPos >= totalLen) {
                seekPos = 0;
                fileIndex += 1;
                if (fileIndex >= originalFiles.length) return true;
                totalLen = originalFiles[fileIndex].length();
                readRaf = new RandomAccessFile(originalFiles[fileIndex], "rw");
                writeRaf = new RandomAccessFile(modifiedFiles[fileIndex], "rw");
            }
            readRaf.seek(seekPos);
            readRaf.read(data);
            fileModifier.modify(data, seekPos, Math.min(data.length, totalLen - seekPos), totalLen);
            writeRaf.seek(seekPos);
            writeRaf.write(data);
            seekPos += data.length;
            modifiedTotalBytes += data.length;

            return true;
        } catch (Exception ex) {
            Log.e("HAHAHA", ex.toString());
            fileModifier.onError(ex);
            return false;
        }
    }

    public boolean open() {
        try {
            seekPos = 0;
            fileIndex = 0;
            totalLen = originalFiles[fileIndex].length();
            readRaf = new RandomAccessFile(originalFiles[fileIndex], "rw");
            writeRaf = new RandomAccessFile(modifiedFiles[fileIndex], "rw");
            return true;
        } catch (Exception ex) {
            Log.e("HAHAHA", ex.toString());
            return false;
        }
    }

    public boolean close() {
        try {
            if (readRaf != null) {
                readRaf.close();
            }
            if (writeRaf != null) {
                writeRaf.close();
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void deleteOriginalFiles() {
        for (File file : originalFiles) file.delete();
    }

    public void deleteModifiedFiles() {
        for (File file : modifiedFiles) file.delete();
    }

    public long getOriginalTotalBytes() {
        return originalTotalBytes;
    }

    public long getModifiedTotalBytes() {
        return cancelled ? originalTotalBytes : modifiedTotalBytes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("--FileGroup--").append('\n');
        sb.append("Total size (byte): ").append(originalTotalBytes).append('\n');
        for (File file : originalFiles) {
            sb.append(file.getAbsolutePath()).append('\n');
        }
        sb.append('\n');
        return sb.toString();
    }

}