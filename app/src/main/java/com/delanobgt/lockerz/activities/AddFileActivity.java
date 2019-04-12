package com.delanobgt.lockerz.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.delanobgt.lockerz.adapters.AddFileAdapter;
import com.delanobgt.lockerz.components.FileExplorerDialog;
import com.delanobgt.lockerz.modules.FileExplorer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFileActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private TextView tvEmpty;
    private RecyclerView recyclerView;
    private AddFileAdapter addFileAdapter;

    private void showFileExplorerDialog() {
        List<FileExplorer.FileItem> fileItems = addFileAdapter.getFileItems();
        Map<String, FileExplorer.FileItem> addedfileItemDict = new HashMap<>();
        for (FileExplorer.FileItem fileItem : fileItems)
            addedfileItemDict.put(fileItem.getFile().getAbsolutePath(), fileItem);

        FileExplorerDialog fileExplorerDialog = new FileExplorerDialog(AddFileActivity.this, addedfileItemDict);
        fileExplorerDialog.setOnSelectedFilesCallback(new FileExplorerDialog.OnSelectedFilesCallback() {
            @Override
            public void callback(List<FileExplorer.FileItem> fileItems) {
                addFileAdapter.addFileItems(fileItems);
            }
        });
        fileExplorerDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        fab = findViewById(R.id.fab_browse_file);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileExplorerDialog();
            }
        });

        tvEmpty = findViewById(R.id.tv_empty);

        recyclerView = findViewById(R.id.recycler_view);
        addFileAdapter = new AddFileAdapter(getApplicationContext(), tvEmpty);
        recyclerView.setAdapter(addFileAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        showFileExplorerDialog();
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
                Toast.makeText(this, "Cancelled adding files", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
