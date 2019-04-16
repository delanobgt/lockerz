package com.delanobgt.lockerz.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.adapters.FileItemAdapter;
import com.delanobgt.lockerz.room.entities.Action;
import com.delanobgt.lockerz.room.entities.FileItem;
import com.delanobgt.lockerz.room.entities.Locker;
import com.delanobgt.lockerz.viewmodels.ActionViewModel;
import com.delanobgt.lockerz.viewmodels.FileItemViewModel;
import com.delanobgt.lockerz.viewmodels.LockerViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.delanobgt.lockerz.activities.AddFileActivity.EXTRA_FILE_ITEMS;

public class LockerDetail extends AppCompatActivity {

    public static final String EXTRA_LOCKER_ID = "com.delanobgt.lockerz.activities.EXTRA_LOCKER_ID";
    public static final int ADD_FILES_REQUEST = 1;

    private NotificationManager notificationManager;
    private Notification.Builder notificationBuilder;

    private Integer lockerId = null;
    private volatile Locker locker = null;
    private Set<Integer> isSelectedSet = new HashSet<>();

    private View viewEmpty;
    private TextView tvEncryptedIn;
    private TextView tvDescribed;
    private RecyclerView recyclerView;
    private FileItemAdapter fileItemAdapter;
    private LockerViewModel lockerViewModel;
    private ActionViewModel actionViewModel;
    private FileItemViewModel fileItemViewModel;
    private FloatingActionButton fab;

    private void showConfirmExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Attention")
                .setMessage("Exit from this locker?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.ic_warning_red_24dp)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker_detail);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = getIntent();
        if (!intent.hasExtra(EXTRA_LOCKER_ID)) {
            Toast.makeText(getApplicationContext(), "Please reopen the app", Toast.LENGTH_SHORT).show();
            finish();
        }
        lockerId = intent.getIntExtra(EXTRA_LOCKER_ID, -1);

        tvEncryptedIn = findViewById(R.id.tv_encrypted_in);
        tvDescribed = findViewById(R.id.tv_described);
        viewEmpty = findViewById(R.id.view_empty);

        recyclerView = findViewById(R.id.recycler_view);
        fileItemAdapter = new FileItemAdapter(LockerDetail.this, viewEmpty);
        fileItemAdapter.setOnSelectedChangeCallback(new FileItemAdapter.OnSelectedChangeCallback() {
            @Override
            public void callback(Set<Integer> pIsSelectedSet) {
                isSelectedSet = pIsSelectedSet;
                renewActionBar();
                invalidateOptionsMenu();
            }
        });
        fileItemAdapter.setOnFileItemEncryptCallback(new FileItemAdapter.OnFileItemEncryptCallback() {
            @Override
            public void callback(FileItem fileItem) {
                fileItem.setEncrypted(true);
                fileItemViewModel.update(fileItem);
            }
        });
        fileItemAdapter.setOnFileItemDecryptCallback(new FileItemAdapter.OnFileItemDecryptCallback() {
            @Override
            public void callback(FileItem fileItem) {
                fileItem.setEncrypted(false);
                fileItemViewModel.update(fileItem);
            }
        });
        fileItemAdapter.setOnFileItemRemovedCallback(new FileItemAdapter.OnFileItemRemovedCallback() {
            @Override
            public void callback(FileItem fileItem) {
                fileItemViewModel.delete(fileItem);
            }
        });
        recyclerView.setAdapter(fileItemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // View Model
        fileItemViewModel = ViewModelProviders.of(this).get(FileItemViewModel.class);
        fileItemViewModel.getAllByLockerId(lockerId).observe(this, new Observer<List<FileItem>>() {
            @Override
            public void onChanged(@Nullable List<FileItem> fileItems) {
                fileItemAdapter.setFileItems(fileItems);
            }
        });

        lockerViewModel = ViewModelProviders.of(this).get(LockerViewModel.class);
        lockerViewModel.getById(lockerId).observe(this, new Observer<Locker>() {
            @Override
            public void onChanged(@Nullable Locker pLocker) {
                if (locker == null) {
                    locker = pLocker;
                    renewActionBar();
                }
                locker = pLocker;
                tvEncryptedIn.setText("Encrypted in " + locker.getEncryptionType());
                tvDescribed.setText(locker.getDescription());
            }
        });

        actionViewModel = ViewModelProviders.of(this).get(ActionViewModel.class);

        fab = findViewById(R.id.fab_add_file);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddFileActivity.class);
                startActivityForResult(intent, ADD_FILES_REQUEST);
            }
        });
    }

    private void renewActionBar() {
        if (isSelectedSet.size() > 0) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorMediumBlue)));
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        } else {
            getSupportActionBar().setTitle(locker.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary)));
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_locker_detail, menu);

        menu.findItem(R.id.item_encrypt).setVisible(false);
        menu.findItem(R.id.item_decrypt).setVisible(false);
        menu.findItem(R.id.item_delete_all).setVisible(false);
        menu.findItem(R.id.item_edit).setVisible(true);
        menu.findItem(R.id.item_change_password).setVisible(true);
        menu.findItem(R.id.item_delete_release).setVisible(true);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        Set<Integer> isSelectedSet = fileItemAdapter.getIsSelectedSet();
        List<FileItem> fileItems = fileItemAdapter.getFileItems();
        Boolean hasEncrypted = false, hasUnEncrypted = false;
        for (Integer pos : isSelectedSet) {
            hasUnEncrypted = true;
        }
        if (hasEncrypted || hasUnEncrypted) {
            menu.findItem(R.id.item_select_all).setVisible(true);
            if (hasUnEncrypted && !hasEncrypted) {
                menu.findItem(R.id.item_encrypt).setVisible(true);
            }
            if (!hasUnEncrypted && hasEncrypted) {
                menu.findItem(R.id.item_decrypt).setVisible(true);
            }
            menu.findItem(R.id.item_delete_all).setVisible(true);
            menu.findItem(R.id.item_edit).setVisible(false);
            menu.findItem(R.id.item_change_password).setVisible(false);
            menu.findItem(R.id.item_delete_release).setVisible(false);
        } else {
            menu.findItem(R.id.item_select_all).setVisible(false);
            menu.findItem(R.id.item_encrypt).setVisible(false);
            menu.findItem(R.id.item_decrypt).setVisible(false);
            menu.findItem(R.id.item_delete_all).setVisible(false);
            menu.findItem(R.id.item_edit).setVisible(true);
            menu.findItem(R.id.item_change_password).setVisible(true);
            menu.findItem(R.id.item_delete_release).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isSelectedSet.size() > 0) {
                    isSelectedSet.clear();
                    renewActionBar();
                    invalidateOptionsMenu();
                    fileItemAdapter.setIsSelectedSet(isSelectedSet);
                } else {
                    showConfirmExitDialog();
                }
                break;
            case R.id.item_select_all:
                int fileItemLength = fileItemAdapter.getFileItems().size();
                for (int i = 0; i < fileItemLength; i++) {
                    isSelectedSet.add(i);
                }
                fileItemAdapter.setIsSelectedSet(isSelectedSet);
                break;
            case R.id.item_encrypt:
                break;
            case R.id.item_decrypt:
                break;
            case R.id.item_edit: {
                Intent intent = new Intent(this, AddEditLockerActivity.class);
                intent.putExtra(AddEditLockerActivity.EXTRA_LOCKER_ID, lockerId);
                startActivity(intent);
                break;
            }
            case R.id.item_change_password: {
                Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                intent.putExtra(AddEditLockerActivity.EXTRA_LOCKER_ID, locker.getId());
                startActivity(intent);
                break;
            }
            case R.id.item_delete_release: {
                new AlertDialog.Builder(this)
                        .setTitle(String.format("Delete %s?", locker.getName()))
                        .setMessage("Are you sure you want to delete this locker?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                lockerViewModel.getById(lockerId).removeObservers(LockerDetail.this);
                                lockerViewModel.delete(locker);
                                actionViewModel.insert(new Action(Action.ActionType.WARNING, String.format("Deleted Locker %s", locker.getName())));
                                Toast.makeText(getApplicationContext(), "Locker deleted", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.ic_warning_red_24dp)
                        .show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_FILES_REQUEST && resultCode == RESULT_OK) {
            final ArrayList<FileItem> fileItems = (ArrayList<FileItem>) data.getSerializableExtra(EXTRA_FILE_ITEMS);
            final int NOTIFICATION_ID = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    for (FileItem fileItem : fileItems) {
                        fileItem.setLockerId(lockerId);
                        fileItem.setEncrypted(true);
                        fileItemViewModel.insert(fileItem);
                    }
                    return null;
                }

                @Override
                protected void onPreExecute() {
                    notificationBuilder = new Notification.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_lock_green_24dp)
                            .setContentTitle("Encryption on progress")
                            .setContentText("...")
                            .setAutoCancel(false)
                            .setOngoing(true)
                            .setProgress(0, 0, true);
                    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    notificationBuilder
                            .setContentTitle("Encryption complete")
                            .setContentText("...")
                            .setAutoCancel(true)
                            .setOngoing(false)
                            .setProgress(0, 0, true);
                    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
                }
            }.execute();

//            Toast.makeText(getApplicationContext(), "File added", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (isSelectedSet.size() > 0) {
            isSelectedSet.clear();
            renewActionBar();
            invalidateOptionsMenu();
            fileItemAdapter.setIsSelectedSet(isSelectedSet);
        } else {
            showConfirmExitDialog();
        }
    }
}
