package com.delanobgt.lockerz.room.converters;

import android.arch.persistence.room.TypeConverter;

import com.delanobgt.lockerz.room.entities.Action;
import com.delanobgt.lockerz.room.entities.Locker;

public class ActionTypeConverter {

    @TypeConverter
    public static Action.ActionType toActionType(int actionType) {
        if (actionType == Action.ActionType.WARNING.getCode()) {
            return Action.ActionType.WARNING;
        } else if (actionType == Action.ActionType.SUCCESS.getCode()) {
            return Action.ActionType.SUCCESS;
        } else if (actionType == Action.ActionType.INFO.getCode()) {
            return Action.ActionType.INFO;
        } else if (actionType == Action.ActionType.ERROR.getCode()) {
            return Action.ActionType.ERROR;
        } else {
            throw new IllegalArgumentException("Could not recognize encryption type!");
        }
    }

    @TypeConverter
    public static int toInteger(Action.ActionType actionType) {
        return actionType.getCode();
    }
}