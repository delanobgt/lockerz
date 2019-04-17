package com.delanobgt.lockerz.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.room.entities.Action;
import com.delanobgt.lockerz.room.entities.Locker;
import com.delanobgt.lockerz.viewmodels.ActionViewModel;
import com.delanobgt.lockerz.viewmodels.LockerViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class AddEditLockerActivity extends AppCompatActivity {
    public static final String EXTRA_LOCKER_ID =
            "com.delanobgt.lockerz.activities.EXTRA_LOCKER_ID";

    private ProgressBar progressBar;

    private EditText etName;
    private TextView tvName;

    private EditText etDescription;
    private TextView tvDescription;

    private Spinner spEncryptionType;

    private EditText etPassword;
    private TextView tvPassword;

    private EditText etConfirmPassword;
    private TextView tvConfirmPassword;

    private EditText etCreatedAt;

    private LockerViewModel lockerViewModel;
    private ActionViewModel actionViewModel;

    private Integer lockerId = null;
    private volatile Locker locker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_locker);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        etName = findViewById(R.id.et_name);
        tvName = findViewById(R.id.tv_name);
        etDescription = findViewById(R.id.et_description);
        tvDescription = findViewById(R.id.tv_description);
        spEncryptionType = findViewById(R.id.sp_encryption_type);
        etPassword = findViewById(R.id.et_password);
        tvPassword = findViewById(R.id.tv_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        tvConfirmPassword = findViewById(R.id.tv_confirm_password);
        etCreatedAt = findViewById(R.id.et_created_at);

        Locker.EncryptionType[] encryptionTypes = Locker.EncryptionType.class.getEnumConstants();
        List<String> spinnerItems = new ArrayList<>();
        for (Locker.EncryptionType encryptionType : encryptionTypes)
            spinnerItems.add(encryptionType.toString());
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getApplicationContext(),
                R.layout.spinner_item,
                spinnerItems
        );
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String defaultEncryptionType = sharedPreferences.getString("encryptionType", "CAESAR");
        int defaultIndex = spinnerItems.indexOf(defaultEncryptionType);
        spEncryptionType.setAdapter(adapter);
        spEncryptionType.setSelection(defaultIndex);

        lockerViewModel = ViewModelProviders.of(this).get(LockerViewModel.class);
        actionViewModel = ViewModelProviders.of(this).get(ActionViewModel.class);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_LOCKER_ID)) {
            lockerId = intent.getIntExtra(EXTRA_LOCKER_ID, -1);

            setTitle("Edit Locker");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

            lockerViewModel.getById(lockerId).observe(this, new Observer<Locker>() {
                @Override
                public void onChanged(@Nullable Locker pLocker) {
                    locker = pLocker;

                    etName.setText(locker.getName());
                    etDescription.setText(locker.getDescription());

                    int spinnerPosition = adapter.getPosition(locker.getEncryptionType().toString());
                    spEncryptionType.setSelection(spinnerPosition);
                    spEncryptionType.setEnabled(false);

                    String formattedDate = new SimpleDateFormat("d MMM yyyy (HH:mm:ss)").format(locker.getCreatedAt());
                    etCreatedAt.setText(formattedDate);
                    etCreatedAt.setEnabled(false);
                }
            });

            etPassword.setVisibility(View.GONE);
            tvPassword.setVisibility(View.GONE);
            etConfirmPassword.setVisibility(View.GONE);
            tvConfirmPassword.setVisibility(View.GONE);
        } else {
            setTitle("Add Locker");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

            etPassword.setVisibility(View.VISIBLE);
            tvPassword.setVisibility(View.VISIBLE);
            etConfirmPassword.setVisibility(View.VISIBLE);
            tvConfirmPassword.setVisibility(View.VISIBLE);

            etCreatedAt.setVisibility(View.GONE);
        }

    }

    private void saveLocker() {
        final String name = etName.getText().toString();
        final String description = etDescription.getText().toString();
        final Locker.EncryptionType encryptionType = Locker.EncryptionType.getEncryptionTypeByString(spEncryptionType.getSelectedItem().toString());
        final String password = lockerId == null ? etPassword.getText().toString() : "12345678";
        final String confirmPassword = lockerId == null ? etConfirmPassword.getText().toString() : "12345678";

        if (!validate(name, description, password, confirmPassword)) {
            Toast.makeText(getApplicationContext(), "There are some form errors", Toast.LENGTH_SHORT).show();
            return;
        }

        if (lockerId == null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    Locker locker = new Locker(name, description, encryptionType, BCrypt.withDefaults().hashToString(8, password.toCharArray()));
                    lockerViewModel.insert(locker);
                    actionViewModel.insert(new Action(Action.ActionType.SUCCESS, String.format("Created Locker %s", locker.getName())));
                    return null;
                }

                @Override
                protected void onPreExecute() {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    progressBar.setVisibility(View.VISIBLE);
                    etName.setEnabled(false);
                    etDescription.setEnabled(false);
                    spEncryptionType.setEnabled(false);
                    etPassword.setEnabled(false);
                    etConfirmPassword.setEnabled(false);
                    etCreatedAt.setEnabled(false);
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Locker created", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    finish();
                }
            }.execute();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            locker.setName(name);
            locker.setDescription(description);
            locker.setEncryptionType(encryptionType);
            lockerViewModel.update(locker);
            actionViewModel.insert(new Action(Action.ActionType.INFO, String.format("Edited Locker %s", locker.getName())));
            Toast.makeText(getApplicationContext(), "Locker saved", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            finish();
        }
    }

    public boolean validate(String name, String description, String password, String confirmPassword) {
        tvName.setText("");
        tvDescription.setText("");
        tvPassword.setText("");
        tvConfirmPassword.setText("");

        boolean valid = true;

        if (name == null || name.isEmpty()) {
            tvName.setText("Name is empty!");
            valid = false;
        } else if (name.length() > 16) {
            tvName.setText("Name can be at most 16 characters!");
            valid = false;
        }

        if (description == null || description.isEmpty()) {
            tvDescription.setText("Description is empty!");
            valid = false;
        } else if (description.length() > 25) {
            tvDescription.setText("Description can be at most 15 characters!");
            valid = false;
        }

        if (password == null || password.isEmpty()) {
            tvPassword.setText("Password is empty!");
            valid = false;
        } else if (password.length() < 8) {
            tvPassword.setText("Password should be at least 8 characters!");
            valid = false;
        }

        if (confirmPassword == null || confirmPassword.isEmpty()) {
            tvConfirmPassword.setText("Please rewrite password!");
            valid = false;
        } else if (password == null || !confirmPassword.equals(password)) {
            tvConfirmPassword.setText("Password doesn't match!");
            valid = false;
        }

        return valid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if (getIntent().hasExtra(EXTRA_LOCKER_ID)) {
            menuInflater.inflate(R.menu.menu_save_locker, menu);
        } else {
            menuInflater.inflate(R.menu.menu_add_locker, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                if (lockerId == null)
                    Toast.makeText(this, "Cancelled creating locker", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Cancelled editing  locker", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_save_locker:
                saveLocker();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}