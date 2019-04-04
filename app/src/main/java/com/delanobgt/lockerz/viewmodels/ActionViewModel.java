package com.delanobgt.lockerz.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.delanobgt.lockerz.room.entities.Action;
import com.delanobgt.lockerz.room.entities.Locker;
import com.delanobgt.lockerz.room.repositories.ActionRepository;
import com.delanobgt.lockerz.room.repositories.LockerRepository;

import java.util.List;

public class ActionViewModel extends AndroidViewModel {
    private ActionRepository repository;
    private LiveData<List<Action>> actions;

    public ActionViewModel(@NonNull Application application) {
        super(application);
        repository = new ActionRepository(application);
        actions = repository.getAll();
    }

    public void insert(Action action) {
        repository.insert(action);
    }

    public void update(Action action) {
        repository.update(action);
    }

    public void delete(Action action) {
        repository.delete(action);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public LiveData<List<Action>> getAll() {
        return actions;
    }
}
