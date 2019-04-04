package com.delanobgt.lockerz.room.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.delanobgt.lockerz.room.DB;
import com.delanobgt.lockerz.room.daos.LockerDao;
import com.delanobgt.lockerz.room.entities.Locker;

import java.util.List;

public class LockerRepository {
    private LockerDao lockerDao;
    private LiveData<List<Locker>> lockers;

    public LockerRepository(Application application) {
        DB database = DB.getInstance(application);
        lockerDao = database.lockerDao();
        lockers = lockerDao.getAll();
    }

    public void insert(Locker locker) {
        (new AsyncTask<Locker, Void, Void>() {
            @Override
            protected Void doInBackground(Locker... lockers) {
                lockerDao.insert(lockers[0]);
                return null;
            }
        }).execute(locker);
    }

    public void update(Locker locker) {
        (new AsyncTask<Locker, Void, Void>() {
            @Override
            protected Void doInBackground(Locker... lockers) {
                lockerDao.update(lockers[0]);
                return null;
            }
        }).execute(locker);
    }

    public void delete(Locker locker) {
        (new AsyncTask<Locker, Void, Void>() {
            @Override
            protected Void doInBackground(Locker... lockers) {
                lockerDao.delete(lockers[0]);
                return null;
            }
        }).execute(locker);
    }

    public void deleteAll() {
        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                lockerDao.deleteAll();
                return null;
            }
        }).execute();
    }

    public LiveData<List<Locker>> getAll() {
        return lockers;
    }

}