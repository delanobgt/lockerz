package com.delanobgt.lockerz.room.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.delanobgt.lockerz.room.DB;
import com.delanobgt.lockerz.room.daos.ActionDao;
import com.delanobgt.lockerz.room.entities.Action;

import java.util.List;

public class ActionRepository {
    private ActionDao actionDao;

    public ActionRepository(Application application) {
        DB database = DB.getInstance(application);
        actionDao = database.actionDao();
    }

    public void insert(final Action action) {
        (new AsyncTask<Action, Void, Void>() {
            @Override
            protected Void doInBackground(Action... actions) {
                actionDao.insert(actions[0]);
                return null;
            }
        }).execute(action);
    }

    public void update(Action action) {
        (new AsyncTask<Action, Void, Void>() {
            @Override
            protected Void doInBackground(Action... actions) {
                actionDao.update(actions[0]);
                return null;
            }
        }).execute(action);
    }

    public void delete(Action action) {
        (new AsyncTask<Action, Void, Void>() {
            @Override
            protected Void doInBackground(Action... actions) {
                actionDao.delete(actions[0]);
                return null;
            }
        }).execute(action);
    }

    public void deleteAll() {
        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                actionDao.deleteAll();
                return null;
            }
        }).execute();
    }

    public LiveData<List<Action>> getAll() {
        return actionDao.getAll();
    }

}