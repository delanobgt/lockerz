package com.delanobgt.lockerz.room.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.delanobgt.lockerz.room.DB;
import com.delanobgt.lockerz.room.daos.FileItemDao;
import com.delanobgt.lockerz.room.entities.FileItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileItemRepository {
    private FileItemDao fileItemDao;
    private LiveData<List<FileItem>> fileItems;
    private Map<Integer, LiveData<List<FileItem>>> fileItemsByLockerId;

    public FileItemRepository(Application application) {
        DB database = DB.getInstance(application);
        fileItemDao = database.fileItemDao();
        fileItems = fileItemDao.getAll();
        fileItemsByLockerId = new HashMap<>();
    }

    public void insert(FileItem fileItem) {
        (new AsyncTask<FileItem, Void, Void>() {
            @Override
            protected Void doInBackground(FileItem... fileItems) {
                fileItemDao.insert(fileItems[0]);
                return null;
            }
        }).execute(fileItem);
    }

    public void update(FileItem fileItem) {
        (new AsyncTask<FileItem, Void, Void>() {
            @Override
            protected Void doInBackground(FileItem... fileItems) {
                fileItemDao.update(fileItems[0]);
                return null;
            }
        }).execute(fileItem);
    }

    public void delete(FileItem fileItem) {
        (new AsyncTask<FileItem, Void, Void>() {
            @Override
            protected Void doInBackground(FileItem... fileItems) {
                fileItemDao.delete(fileItems[0]);
                return null;
            }
        }).execute(fileItem);
    }

    public void deleteAll() {
        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                fileItemDao.deleteAll();
                return null;
            }
        }).execute();
    }

    public LiveData<List<FileItem>> getAll() {
        return fileItems;
    }

    public LiveData<List<FileItem>> getAllByLockerId(int lockerId) {
        if (!fileItemsByLockerId.containsKey(lockerId)) {
            fileItemsByLockerId.put(lockerId, fileItemDao.getAllByLockerId(lockerId));
        }
        return fileItemsByLockerId.get(lockerId);
    }

}