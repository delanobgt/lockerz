package com.delanobgt.lockerz.room.converters;

import android.arch.persistence.room.TypeConverter;

import com.delanobgt.lockerz.room.entities.FileItem;


public class FileItemTypeConverter {

    @TypeConverter
    public static FileItem.FileItemType toFileItemType(int fileItemType) {
        if (fileItemType == FileItem.FileItemType.DIRECTORY.getCode()) {
            return FileItem.FileItemType.DIRECTORY;
        } else if (fileItemType == FileItem.FileItemType.FILE.getCode()) {
            return FileItem.FileItemType.FILE;
        } else {
            throw new IllegalArgumentException("Could not recognize file type!");
        }
    }

    @TypeConverter
    public static int toInteger(FileItem.FileItemType fileItemType) {
        return fileItemType.getCode();
    }
}