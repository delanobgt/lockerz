package com.delanobgt.lockerz.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.delanobgt.lockerz.room.entities.FileItem;
import com.delanobgt.lockerz.room.repositories.FileItemRepository;

import java.util.List;

public class FileItemViewModel extends AndroidViewModel {
    private FileItemRepository repository;

    public FileItemViewModel(@NonNull Application application) {
        super(application);
        repository = new FileItemRepository(application);
    }

    public void insert(FileItem fileItem) {
        repository.insert(fileItem);
    }

    public void update(FileItem fileItem) {
        repository.update(fileItem);
    }

    public void delete(FileItem fileItem) {
        repository.delete(fileItem);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public LiveData<List<FileItem>> getAll() {
        return repository.getAll();
    }

    public LiveData<List<FileItem>> getAllByLockerId(int lockerId) {
        return repository.getAllByLockerId(lockerId);
    }

}
