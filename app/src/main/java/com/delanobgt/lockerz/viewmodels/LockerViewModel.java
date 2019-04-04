package com.delanobgt.lockerz.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.delanobgt.lockerz.room.entities.Locker;
import com.delanobgt.lockerz.room.repositories.LockerRepository;

import java.util.List;

public class LockerViewModel extends AndroidViewModel {
    private LockerRepository repository;
    private LiveData<List<Locker>> lockers;

    public LockerViewModel(@NonNull Application application) {
        super(application);
        repository = new LockerRepository(application);
        lockers = repository.getAll();
    }

    public void insert(Locker locker) {
        repository.insert(locker);
    }

    public void update(Locker locker) {
        repository.update(locker);
    }

    public void delete(Locker locker) {
        repository.delete(locker);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public LiveData<List<Locker>> getAll() {
        return lockers;
    }
}
