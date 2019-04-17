package com.delanobgt.lockerz.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.activities.AddEditLockerActivity;
import com.delanobgt.lockerz.activities.ChangePasswordActivity;
import com.delanobgt.lockerz.activities.LockerDetail;
import com.delanobgt.lockerz.adapters.LockerAdapter;
import com.delanobgt.lockerz.components.PasswordLoginDialog;
import com.delanobgt.lockerz.room.entities.Action;
import com.delanobgt.lockerz.room.entities.Locker;
import com.delanobgt.lockerz.viewmodels.ActionViewModel;
import com.delanobgt.lockerz.viewmodels.LockerViewModel;

import java.util.List;

public class LockerListFragment extends Fragment {

    private View viewEmpty;
    private RecyclerView recyclerView;
    private LockerAdapter lockerAdapter;
    private FloatingActionButton fab;
    private LockerViewModel lockerViewModel;
    private ActionViewModel actionViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locker_list, container, false);

        viewEmpty = view.findViewById(R.id.view_empty);

        recyclerView = view.findViewById(R.id.recycler_view_locker);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        lockerAdapter = new LockerAdapter(getContext(), viewEmpty);
        lockerAdapter.setOnLockerSelectedCallback(new LockerAdapter.OnLockerSelectedCallback() {
            @Override
            public void onLockerSelected(final Locker locker) {
                PasswordLoginDialog passwordLoginDialog = new PasswordLoginDialog(getActivity(), locker);
                passwordLoginDialog.setOnLoginSuccessCallback(new PasswordLoginDialog.OnLoginSuccessCallback() {
                    @Override
                    public void callback(String password) {
                        Intent intent = new Intent(getContext(), LockerDetail.class);
                        intent.putExtra(LockerDetail.EXTRA_LOCKER_ID, locker.getId());
                        startActivity(intent);
                    }
                });
                passwordLoginDialog.show();
            }
        });
        lockerAdapter.setOnLockerEditCallback(new LockerAdapter.OnLockerEditCallback() {
            @Override
            public void onLockerEdit(final Locker locker) {
                PasswordLoginDialog passwordLoginDialog = new PasswordLoginDialog(getActivity(), locker);
                passwordLoginDialog.setOnLoginSuccessCallback(new PasswordLoginDialog.OnLoginSuccessCallback() {
                    @Override
                    public void callback(String password) {
                        Intent intent = new Intent(getContext(), AddEditLockerActivity.class);
                        intent.putExtra(AddEditLockerActivity.EXTRA_LOCKER_ID, locker.getId());
                        startActivity(intent);
                    }
                });
                passwordLoginDialog.show();
            }
        });
        lockerAdapter.setOnLockerChangePasswordCallback(new LockerAdapter.OnLockerChangePasswordCallback() {
            @Override
            public void callback(final Locker locker) {
                Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
                intent.putExtra(AddEditLockerActivity.EXTRA_LOCKER_ID, locker.getId());
                startActivity(intent);
            }
        });
        lockerAdapter.setOnLockerDeleteCallback(new LockerAdapter.OnLockerDeleteCallback() {
            @Override
            public void onLockerDelete(final Locker locker) {
                new AlertDialog.Builder(getContext())
                        .setTitle(String.format("Delete %s?", locker.getName()))
                        .setMessage("Are you sure you want to delete this locker?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        PasswordLoginDialog passwordLoginDialog = new PasswordLoginDialog(getActivity(), locker);
                                        passwordLoginDialog.setOnLoginSuccessCallback(new PasswordLoginDialog.OnLoginSuccessCallback() {
                                            @Override
                                            public void callback(String password) {
                                                lockerViewModel.delete(locker);
                                                actionViewModel.insert(new Action(Action.ActionType.WARNING, String.format("Deleted Locker %s", locker.getName())));
                                                Toast.makeText(getContext(), "Locker deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        passwordLoginDialog.show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.ic_warning_red_24dp)
                        .show();
            }
        });
        recyclerView.setAdapter(lockerAdapter);

        lockerViewModel = ViewModelProviders.of(getActivity()).get(LockerViewModel.class);
        lockerViewModel.getAll().observe(this, new Observer<List<Locker>>() {
            @Override
            public void onChanged(@Nullable List<Locker> lockers) {
                lockerAdapter.setLockers(lockers);
            }
        });

        actionViewModel = ViewModelProviders.of(getActivity()).get(ActionViewModel.class);

        fab = view.findViewById(R.id.fab_add_locker);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddEditLockerActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
