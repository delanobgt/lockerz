package com.delanobgt.lockerz.room.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.delanobgt.lockerz.room.entities.FileItem;

import java.util.List;

@Dao
public interface FileItemDao {

    @Insert
    void insert(FileItem fileItem);

    @Update
    void update(FileItem fileItem);

    @Delete
    void delete(FileItem fileItem);

    @Query("DELETE FROM fileItems")
    void deleteAll();

    @Query("SELECT * FROM fileItems")
    LiveData<List<FileItem>> getAll();

    @Query("SELECT * FROM fileItems WHERE lockerId = :lockerId")
    LiveData<List<FileItem>> getAllByLockerId(int lockerId);

}