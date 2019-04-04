package com.delanobgt.lockerz.room.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.delanobgt.lockerz.room.entities.Action;
import com.delanobgt.lockerz.room.entities.Locker;

import java.util.List;

@Dao
public interface ActionDao {

    @Insert
    void insert(Action action);

    @Update
    void update(Action action);

    @Delete
    void delete(Action action);

    @Query("DELETE FROM actions")
    void deleteAll();

    @Query("SELECT * FROM actions ORDER BY createdAt DESC")
    LiveData<List<Action>> getAll();
}