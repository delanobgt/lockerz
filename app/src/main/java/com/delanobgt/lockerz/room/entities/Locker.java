package com.delanobgt.lockerz.room.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.delanobgt.lockerz.room.converters.DateConverter;
import com.delanobgt.lockerz.room.converters.EncryptionTypeConverter;

import java.util.Date;

@Entity(tableName = "lockers")
public class Locker {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    @NonNull
    private String description;

    @NonNull
    @TypeConverters(EncryptionTypeConverter.class)
    private String encryptionType;

    @NonNull
    @TypeConverters(DateConverter.class)
    private Date createdAt = new Date();

    public Locker(@NonNull String name, @NonNull String description, String encryptionType) {
        this.name = name;
        this.description = description;
        this.encryptionType = encryptionType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public String getEncryptionType() {
        return encryptionType;
    }

    public void setCreatedAt(@NonNull Date createdAt) {
        this.createdAt = createdAt;
    }

    @NonNull
    public Date getCreatedAt() {
        return createdAt;
    }

    public enum EncryptionType {
        AES(0);

        private int code;

        EncryptionType(int code) {
            this.code = code;
        }
        public int getCode() {
            return code;
        }
    }
}
