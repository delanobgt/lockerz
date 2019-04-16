package com.delanobgt.lockerz.components;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.room.entities.Locker;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordLoginDialog extends Dialog {

    private EditText etPassword;
    private TextView tvPassword;
    private ProgressBar progressBar;
    private Button btnLogin;
    private Button btnCancel;
    private OnLoginSuccessCallback onLoginSuccessCallback;
    private Locker locker;

    public PasswordLoginDialog(Activity activity, Locker locker) {
        super(activity);
        this.locker = locker;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_password_login);

        etPassword = findViewById(R.id.et_password);
        tvPassword = findViewById(R.id.tv_password);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvPassword.setText("");
                final String password = etPassword.getText().toString();
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), locker.getPasswordHash());
                        if (result.verified && onLoginSuccessCallback != null) {
                            onLoginSuccessCallback.callback(password);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        btnLogin.setEnabled(false);
                        btnCancel.setEnabled(false);
                        etPassword.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected void onPostExecute(Boolean successLogin) {
                        btnLogin.setEnabled(true);
                        btnCancel.setEnabled(true);
                        etPassword.setEnabled(true);
                        if (successLogin) {
                            dismiss();
                        } else {
                            etPassword.setText("");
                            tvPassword.setText("Wrong password!");
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }.execute();
            }
        });

        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setOnLoginSuccessCallback(OnLoginSuccessCallback onLoginSuccessCallback) {
        this.onLoginSuccessCallback = onLoginSuccessCallback;
    }

    public interface OnLoginSuccessCallback {
        void callback(String password);
    }
}
