package com.delanobgt.lockerz.room.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.delanobgt.lockerz.room.converters.ActionTypeConverter;
import com.delanobgt.lockerz.room.converters.DateConverter;
import com.delanobgt.lockerz.room.converters.EncryptionTypeConverter;

import java.util.Date;

@Entity(tableName = "actions")
public class Action {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @TypeConverters(ActionTypeConverter.class)
    private ActionType actionType;

    @NonNull
    private String description;

    @NonNull
    @TypeConverters(DateConverter.class)
    private Date createdAt = new Date();

    public Action(@NonNull ActionType actionType, @NonNull String description) {
        this.actionType = actionType;
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public ActionType getActionType() {
        return actionType;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NonNull Date createdAt) {
        this.createdAt = createdAt;
    }

    public enum ActionType {
        ERROR(0),
        INFO(1),
        SUCCESS(2),
        WARNING(3);

        private int code;

        ActionType(int code) {
            this.code = code;
        }
        public int getCode() {
            return code;
        }
    }
}
