package com.delanobgt.lockerz.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.activities.AddEditLockerActivity;
import com.delanobgt.lockerz.adapters.ActionAdapter;
import com.delanobgt.lockerz.adapters.LockerAdapter;
import com.delanobgt.lockerz.room.entities.Action;
import com.delanobgt.lockerz.room.entities.Locker;
import com.delanobgt.lockerz.viewmodels.ActionViewModel;
import com.delanobgt.lockerz.viewmodels.LockerViewModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class RecentActionListFragment extends Fragment {

    private ActionViewModel actionViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_activity_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_action);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        final ActionAdapter adapter = new ActionAdapter(getContext());
        recyclerView.setAdapter(adapter);

        actionViewModel = ViewModelProviders.of(getActivity()).get(ActionViewModel.class);
        actionViewModel.getAll().observe(getActivity(), new Observer<List<Action>>() {
            @Override
            public void onChanged(@Nullable List<Action> action) {
                adapter.setActions(action);
            }
        });

        return view;
    }

}
