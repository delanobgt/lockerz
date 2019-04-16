package com.delanobgt.lockerz.room.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.delanobgt.lockerz.room.DB;
import com.delanobgt.lockerz.room.daos.LockerDao;
import com.delanobgt.lockerz.room.entities.Locker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LockerRepository {
    private LockerDao lockerDao;
    private LiveData<List<Locker>> lockers;
    private Map<Integer, LiveData<Locker>> lockersById;

    public LockerRepository(Application application) {
        DB database = DB.getInstance(application);
        lockerDao = database.lockerDao();
        lockers = lockerDao.getAll();
        lockersById = new HashMap<>();
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

    public LiveData<Locker> getById(int id) {
        if (!lockersById.containsKey(id)) {
            lockersById.put(id, lockerDao.getById(id));
        }
        return lockersById.get(id);
    }
}