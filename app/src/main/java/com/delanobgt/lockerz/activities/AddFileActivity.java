package com.delanobgt.lockerz.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.adapters.AddFileAdapter;
import com.delanobgt.lockerz.components.FileExplorerDialog;
import com.delanobgt.lockerz.room.entities.FileItem;
import com.delanobgt.lockerz.viewmodels.FileItemViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFileActivity extends AppCompatActivity {

    public static final String EXTRA_FILE_ITEMS = "com.delanobgt.lockerz.activities.EXTRA_FILE_ITEMS";

    private FloatingActionButton fab;
    private View viewEmpty;
    private RecyclerView recyclerView;
    private AddFileAdapter addFileAdapter;
    private FileItemViewModel fileItemViewModel;
    private volatile List<FileItem> otherAddedFileItems = null;

    private void showFileExplorerDialog() {
        List<FileItem> addedFileItems = addFileAdapter.getFileItems();
        addedFileItems.addAll(otherAddedFileItems);
        Map<String, FileItem> addedFileItemDict = new HashMap<>();
        for (FileItem fileItem : addedFileItems)
            addedFileItemDict.put(fileItem.getFile().getAbsolutePath(), fileItem);

        FileExplorerDialog fileExplorerDialog = new FileExplorerDialog(this, addedFileItemDict);
        fileExplorerDialog.setOnSelectedFilesCallback(new FileExplorerDialog.OnSelectedFilesCallback() {
            @Override
            public void callback(List<FileItem> fileItems) {
                addFileAdapter.addFileItems(fileItems);
            }
        });
        fileExplorerDialog.show();
    }

    private void showConfirmExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Attention")
                .setMessage("Cancel adding files?")
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
        setContentView(R.layout.activity_add_file);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        fileItemViewModel = ViewModelProviders.of(this).get(FileItemViewModel.class);
        fileItemViewModel.getAll().observe(this, new Observer<List<FileItem>>() {
            @Override
            public void onChanged(@Nullable List<FileItem> fileItems) {
                if (otherAddedFileItems == null) {
                    otherAddedFileItems = fileItems;
                    showFileExplorerDialog();
                } else {
                    otherAddedFileItems = fileItems;
                }
            }
        });

        fab = findViewById(R.id.fab_browse_file);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileExplorerDialog();
            }
        });

        viewEmpty = findViewById(R.id.view_empty);

        recyclerView = findViewById(R.id.recycler_view);
        addFileAdapter = new AddFileAdapter(getApplicationContext(), viewEmpty);
        recyclerView.setAdapter(addFileAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showConfirmExitDialog();
                break;
            case R.id.item_save_added_file:
                ArrayList<FileItem> fileItems = (ArrayList<FileItem>) addFileAdapter.getFileItems();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_FILE_ITEMS, fileItems);
                setResult(RESULT_OK, resultIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showConfirmExitDialog();
    }
}
