package com.delanobgt.lockerz.room.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.delanobgt.lockerz.room.converters.FileItemTypeConverter;

import java.io.File;
import java.io.Serializable;

@Entity(tableName = "fileItems",
        indices = {@Index("lockerId")},
        foreignKeys = @ForeignKey(entity = Locker.class,
                parentColumns = "id",
                childColumns = "lockerId",
                onDelete = ForeignKey.CASCADE))
public class FileItem implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String path;

    @NonNull
    @TypeConverters(FileItemTypeConverter.class)
    private FileItemType type;

    @NonNull
    private int lockerId;

    @NonNull
    private boolean encrypted;

    @Ignore
    private File file;

    public FileItem() {
    }

    public FileItem(String path, FileItemType type) {
        this.path = path;
        this.type = type;
        this.file = new File(path);
    }

    public FileItem(@NonNull String path, @NonNull FileItemType type, int lockerId, boolean encrypted) {
        this.path = path;
        this.type = type;
        this.lockerId = lockerId;
        this.encrypted = encrypted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getPath() {
        return path;
    }

    public void setPath(@NonNull String path) {
        this.path = path;
        this.file = new File(path);
    }

    @NonNull
    public FileItemType getType() {
        return type;
    }

    public void setType(@NonNull FileItemType type) {
        this.type = type;
    }

    public int getLockerId() {
        return lockerId;
    }

    public void setLockerId(int lockerId) {
        this.lockerId = lockerId;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public enum FileItemType {
        FILE(0),
        DIRECTORY(1);

        private final int code;

        FileItemType(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        @Override
        public String toString() {
            if (this == FILE) return "File";
            if (this == DIRECTORY) return "Directory";
            return null;
        }
    }
}
