package com.delanobgt.lockerz.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.delanobgt.lockerz.room.daos.ActionDao;
import com.delanobgt.lockerz.room.daos.LockerDao;
import com.delanobgt.lockerz.room.entities.Action;
import com.delanobgt.lockerz.room.entities.Locker;

@Database(entities = {Locker.class, Action.class}, version = 1)
public abstract class DB extends RoomDatabase {

    private static DB instance;

    public abstract LockerDao lockerDao();
    public abstract ActionDao actionDao();

    public static synchronized DB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    DB.class, "DB")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private ActionDao actionDao;
        private LockerDao lockerDao;

        private PopulateDbAsyncTask(DB db) {
            actionDao = db.actionDao();
            lockerDao = db.lockerDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            lockerDao.insert(new Locker("Locker 1", "Lorem ipsum dolor sit a met", "AES"));
            lockerDao.insert(new Locker("Locker 2", "Lorem ipsum dolor sit a met", "AES"));
            lockerDao.insert(new Locker("Locker 3", "Lorem ipsum dolor sit a met", "AES"));
            return null;
        }
    }
}