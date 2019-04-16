package com.delanobgt.lockerz.room.converters;

import android.arch.persistence.room.TypeConverter;

import com.delanobgt.lockerz.room.entities.Locker;

public class EncryptionTypeConverter {

    @TypeConverter
    public static Locker.EncryptionType toEncryptionType(int encryptionType) {
        if (encryptionType == Locker.EncryptionType.AES.getCode()) {
            return Locker.EncryptionType.AES;
        } else if (encryptionType == Locker.EncryptionType.CAESAR.getCode()) {
            return Locker.EncryptionType.CAESAR;
        } else if (encryptionType == Locker.EncryptionType.DOUBLE_TRANSPOSITION.getCode()) {
            return Locker.EncryptionType.DOUBLE_TRANSPOSITION;
        } else {
            throw new IllegalArgumentException("Could not recognize encryption type!");
        }
    }

    @TypeConverter
    public static int toInteger(Locker.EncryptionType encryptionType) {
        return encryptionType.getCode();
    }
}