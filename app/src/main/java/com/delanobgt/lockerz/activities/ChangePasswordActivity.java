package com.delanobgt.lockerz.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.room.entities.Action;
import com.delanobgt.lockerz.room.entities.Locker;
import com.delanobgt.lockerz.viewmodels.ActionViewModel;
import com.delanobgt.lockerz.viewmodels.LockerViewModel;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class ChangePasswordActivity extends AppCompatActivity {
    public static final String EXTRA_LOCKER_ID =
            "com.delanobgt.lockerz.activities.EXTRA_LOCKER_ID";

    private ProgressBar progressBar;

    private EditText etOldPassword;
    private TextView tvOldPassword;

    private EditText etNewPassword;
    private TextView tvNewPassword;

    private EditText etConfirmNewPassword;
    private TextView tvConfirmNewPassword;

    private LockerViewModel lockerViewModel;
    private ActionViewModel actionViewModel;

    private Integer lockerId = null;
    private volatile Locker locker = null;

    private void showConfirmExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Attention")
                .setMessage("Cancel changing password?")
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
        setContentView(R.layout.activity_change_password);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        etOldPassword = findViewById(R.id.et_old_password);
        tvOldPassword = findViewById(R.id.tv_old_password);
        etNewPassword = findViewById(R.id.et_new_password);
        tvNewPassword = findViewById(R.id.tv_new_password);
        etConfirmNewPassword = findViewById(R.id.et_confirm_new_password);
        tvConfirmNewPassword = findViewById(R.id.tv_confirm_new_password);

        lockerViewModel = ViewModelProviders.of(this).get(LockerViewModel.class);
        actionViewModel = ViewModelProviders.of(this).get(ActionViewModel.class);

        Intent intent = getIntent();
        lockerId = intent.getIntExtra(EXTRA_LOCKER_ID, -1);

        setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        lockerViewModel.getById(lockerId).observe(this, new Observer<Locker>() {
            @Override
            public void onChanged(@Nullable Locker pLocker) {
                locker = pLocker;
            }
        });
    }

    private void savePassword() {
        final String oldPassword = etOldPassword.getText().toString();
        final String newPassword = etNewPassword.getText().toString();
        final String confirmNewPassword = etConfirmNewPassword.getText().toString();

        if (!validate(oldPassword, newPassword, confirmNewPassword)) {
            Toast.makeText(getApplicationContext(), "There are some form errors", Toast.LENGTH_SHORT).show();
            return;
        }

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                BCrypt.Result result = BCrypt.verifyer().verify(oldPassword.toCharArray(), locker.getPasswordHash());
                if (result.verified) {
                    locker.setPasswordHash(BCrypt.withDefaults().hashToString(8, newPassword.toCharArray()));
                    lockerViewModel.update(locker);
                    actionViewModel.insert(new Action(Action.ActionType.INFO, String.format("Changed Password for Locker %s", locker.getName())));
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            protected void onPreExecute() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                progressBar.setVisibility(View.VISIBLE);
                etOldPassword.setEnabled(false);
                etNewPassword.setEnabled(false);
                etConfirmNewPassword.setEnabled(false);
            }

            @Override
            protected void onPostExecute(Boolean successLogin) {
                progressBar.setVisibility(View.GONE);
                etOldPassword.setEnabled(true);
                etNewPassword.setEnabled(true);
                etConfirmNewPassword.setEnabled(true);
                if (successLogin) {
                    Toast.makeText(getApplicationContext(), "Password changed", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    tvOldPassword.setText("Old Password doesn't match!");
                }
            }
        }.execute();
    }

    public boolean validate(String oldPassword, String newPassword, String confirmNewPassword) {
        tvOldPassword.setText("");
        tvNewPassword.setText("");
        tvConfirmNewPassword.setText("");

        boolean valid = true;

        if (oldPassword == null || oldPassword.isEmpty()) {
            tvOldPassword.setText("Old Password is empty!");
            valid = false;
        }

        if (newPassword == null || newPassword.isEmpty()) {
            tvNewPassword.setText("New Password is empty!");
            valid = false;
        } else if (newPassword.length() < 8) {
            tvNewPassword.setText("New Password should be at least 8 characters!");
            valid = false;
        }

        if (confirmNewPassword == null || confirmNewPassword.isEmpty()) {
            tvConfirmNewPassword.setText("Please rewrite password!");
            valid = false;
        } else if (newPassword == null || !confirmNewPassword.equals(newPassword)) {
            tvConfirmNewPassword.setText("Password doesn't match!");
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
                Toast.makeText(this, "Cancelled changing password", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_save_locker:
                savePassword();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showConfirmExitDialog();
    }
}