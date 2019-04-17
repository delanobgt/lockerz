package com.delanobgt.lockerz.room.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.delanobgt.lockerz.room.converters.DateConverter;
import com.delanobgt.lockerz.room.converters.EncryptionTypeConverter;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity(tableName = "lockers")
public class Locker implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    @NonNull
    private String description;

    @NonNull
    @TypeConverters(EncryptionTypeConverter.class)
    private EncryptionType encryptionType;

    @NonNull
    private String passwordHash;

    @NonNull
    @TypeConverters(DateConverter.class)
    private Date createdAt = new Date();

    public Locker(@NonNull String name, @NonNull String description, EncryptionType encryptionType, String passwordHash) {
        this.name = name;
        this.description = description;
        this.encryptionType = encryptionType;
        this.passwordHash = passwordHash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    @NonNull
    public EncryptionType getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(@NonNull EncryptionType encryptionType) {
        this.encryptionType = encryptionType;
    }

    @NonNull
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(@NonNull String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @NonNull
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NonNull Date createdAt) {
        this.createdAt = createdAt;
    }

    public enum EncryptionType {
        CAESAR(0),
        VIGENERE(1),
        XOR(2);

        private static Map<String, EncryptionType> stringMap;

        static {
            stringMap = new HashMap<>();
            for (EncryptionType encryptionType : EncryptionType.class.getEnumConstants()) {
                stringMap.put(encryptionType.toString(), encryptionType);
            }
        }

        private int code;

        public static EncryptionType getEncryptionTypeByString(String encryptionType) {
            return stringMap.get(encryptionType);
        }

        EncryptionType(int code) {
            this.code = code;
        }
        public int getCode() {
            return code;
        }
    }
}
