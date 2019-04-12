package com.delanobgt.lockerz.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.components.FileExplorerDialog;

public class AddFileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        FloatingActionButton fab = findViewById(R.id.fab_browse_file);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileExplorerDialog fileExplorerDialog = new FileExplorerDialog(AddFileActivity.this);
                fileExplorerDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu_add_locker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this, "Leaved", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
