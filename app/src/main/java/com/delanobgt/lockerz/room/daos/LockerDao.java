package com.delanobgt.lockerz.room.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.delanobgt.lockerz.room.entities.Locker;

import java.util.List;

@Dao
public interface LockerDao {

    @Insert
    void insert(Locker locker);

    @Update
    void update(Locker locker);

    @Delete
    void delete(Locker locker);

    @Query("DELETE FROM lockers")
    void deleteAll();

    @Query("SELECT * FROM lockers ORDER BY name ASC")
    LiveData<List<Locker>> getAll();

    @Query("SELECT * FROM lockers WHERE id = :id")
    LiveData<Locker> getById(int id);

}