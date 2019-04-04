package com.delanobgt.lockerz.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.delanobgt.lockerz.adapters.LockerAdapter;
import com.delanobgt.lockerz.room.entities.Locker;
import com.delanobgt.lockerz.viewmodels.LockerViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class LockerListFragment extends Fragment {

    public static final int ADD_LOCKER_REQUEST = 1;
    public static final int EDIT_LOCKER_REQUEST = 2;

    private LockerViewModel lockerViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locker_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_locker);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        final LockerAdapter adapter = new LockerAdapter(getContext());
        adapter.setOnLockerEditCallback(new LockerAdapter.OnLockerEditCallback() {
            @Override
            public void onLockerEdit(Locker locker) {
                Intent intent = new Intent(getContext(), AddEditLockerActivity.class);
                intent.putExtra(AddEditLockerActivity.EXTRA_ID, locker.getId());
                intent.putExtra(AddEditLockerActivity.EXTRA_NAME, locker.getName());
                intent.putExtra(AddEditLockerActivity.EXTRA_DESCRIPTION, locker.getDescription());
                intent.putExtra(AddEditLockerActivity.EXTRA_ENCRYPTION_TYPE, locker.getEncryptionType());

                String formattedDate = new SimpleDateFormat("d MMM yyyy (HH:mm:ss)").format(locker.getCreatedAt());
                intent.putExtra(AddEditLockerActivity.EXTRA_CREATED_AT, formattedDate);

                startActivityForResult(intent, EDIT_LOCKER_REQUEST);
            }
        });
        adapter.setOnLockerDeleteCallback(new LockerAdapter.OnLockerDeleteCallback() {
            @Override
            public void onLockerDelete(final Locker locker) {
                new AlertDialog.Builder(getContext())
                        .setTitle(String.format("Delete %s?", locker.getName()))
                        .setMessage("Are you sure you want to delete this locker?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                lockerViewModel.delete(locker);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.ic_warning_red_24dp)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        lockerViewModel = ViewModelProviders.of(this).get(LockerViewModel.class);
        lockerViewModel.getAll().observe(this, new Observer<List<Locker>>() {
            @Override
            public void onChanged(@Nullable List<Locker> lockers) {
                adapter.setLockers(lockers);
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fab_add_locker);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddEditLockerActivity.class);
                startActivityForResult(intent, ADD_LOCKER_REQUEST);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_LOCKER_REQUEST && resultCode == RESULT_OK) {
            String title = data.getStringExtra(AddEditLockerActivity.EXTRA_NAME);
            String description = data.getStringExtra(AddEditLockerActivity.EXTRA_DESCRIPTION);
            String encryptionType = data.getStringExtra(AddEditLockerActivity.EXTRA_ENCRYPTION_TYPE);

            Locker note = new Locker(title, description, encryptionType);
            lockerViewModel.insert(note);

            Toast.makeText(getContext(), "Locker saved", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_LOCKER_REQUEST && resultCode == RESULT_OK) {
            if (!data.hasExtra(AddEditLockerActivity.EXTRA_ID)) {
                Toast.makeText(getContext(), "Locker can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }

            int id = data.getIntExtra(AddEditLockerActivity.EXTRA_ID, -1);
            String name = data.getStringExtra(AddEditLockerActivity.EXTRA_NAME);
            String description = data.getStringExtra(AddEditLockerActivity.EXTRA_DESCRIPTION);
            String encyptionType = data.getStringExtra(AddEditLockerActivity.EXTRA_ENCRYPTION_TYPE);

            Locker locker = new Locker(name, description, encyptionType);
            locker.setId(id);
            lockerViewModel.update(locker);

            Toast.makeText(getContext(), "Locker updated", Toast.LENGTH_SHORT).show();
        }
    }


}
