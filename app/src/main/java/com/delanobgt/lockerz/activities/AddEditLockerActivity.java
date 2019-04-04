package com.delanobgt.lockerz.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.room.entities.Locker;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddEditLockerActivity extends AppCompatActivity {
    public static final String EXTRA_ID =
            "com.delanobgt.lockerz.activities.EXTRA_ID";
    public static final String EXTRA_NAME =
            "com.delanobgt.lockerz.activities.EXTRA_NAME";
    public static final String EXTRA_DESCRIPTION =
            "com.delanobgt.lockerz.activities.EXTRA_DESCRIPTION";
    public static final String EXTRA_ENCRYPTION_TYPE =
            "com.delanobgt.lockerz.activities.EXTRA_ENCRYPTION_TYPE";
    public static final String EXTRA_CREATED_AT =
            "com.delanobgt.lockerz.activities.EXTRA_CREATED_AT";

    private EditText etName;
    private EditText etDescription;
    private Spinner spEncryptionType;
    private EditText etCreatedAt;
    private TextView tvName;
    private TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_locker);

        etName = findViewById(R.id.et_name);
        etDescription = findViewById(R.id.et_description);
        spEncryptionType = findViewById(R.id.sp_encryption_type);
        etCreatedAt = findViewById(R.id.et_created_at);
        tvName = findViewById(R.id.tv_name);
        tvDescription = findViewById(R.id.tv_description);

        Locker.EncryptionType[] encryptionTypes = Locker.EncryptionType.class.getEnumConstants();
        List<String> spinnerItems = new ArrayList<>();
        for (Locker.EncryptionType encryptionType : encryptionTypes)
            spinnerItems.add(encryptionType.toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getApplicationContext(),
                R.layout.spinner_item,
                spinnerItems
        );
        spEncryptionType.setAdapter(adapter);
        spEncryptionType.setSelection(0);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            setTitle("Edit Locker");

            etName.setText(intent.getStringExtra(EXTRA_NAME));
            etDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));

            int spinnerPosition = adapter.getPosition(intent.getStringExtra(EXTRA_ENCRYPTION_TYPE));
            spEncryptionType.setSelection(spinnerPosition);
            spEncryptionType.setEnabled(false);

            etCreatedAt.setText(intent.getStringExtra(EXTRA_CREATED_AT));
            etCreatedAt.setEnabled(false);
        } else {
            setTitle("Add Locker");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
            etCreatedAt.setVisibility(View.GONE);
        }

    }

    private void saveLocker() {
        String name = etName.getText().toString();
        String description = etDescription.getText().toString();
        String encryptionType = spEncryptionType.getSelectedItem().toString();

        if (!validate(name, description)) {
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_NAME, name);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_ENCRYPTION_TYPE, encryptionType);

        if (getIntent().hasExtra(EXTRA_ID))
            data.putExtra(EXTRA_ID, getIntent().getIntExtra(EXTRA_ID, -1));

        setResult(RESULT_OK, data);
        finish();
    }

    public boolean validate(String name, String description) {
        tvName.setText("");
        tvDescription.setText("");

        boolean valid = true;

        if (name == null || name.isEmpty()) {
            tvName.setText("Name is empty!");
            valid = false;
        } else if (name.length() > 16) {
            tvName.setText("Name can be at most 16 characters!");
            valid = false;
        }

        if (description == null || description .isEmpty()) {
            tvDescription.setText("Description is empty!");
            valid = false;
        } else if (description.length() > 25) {
            tvName.setText("Description can be at most 15 characters!");
            valid = false;
        }

        return valid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if (getIntent().hasExtra(EXTRA_ID)) {
            menuInflater.inflate(R.menu.menu_save_locker, menu);
        } else {
            menuInflater.inflate(R.menu.menu_add_locker, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save_locker:
                saveLocker();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}