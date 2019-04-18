package com.delanobgt.lockerz.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.adapters.WorkerProgressAdapter;
import com.delanobgt.lockerz.modules.WorkableFileGroup;
import com.delanobgt.lockerz.services.DecryptWorkerService;
import com.delanobgt.lockerz.services.EncryptWorkerService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkerProgressActivity extends AppCompatActivity {

    public static final String EXTRA_WORK_TYPE = "com.delanobgt.lockerz.activities.EXTRA_WORK_TYPE";

    private volatile EncryptWorkerService encryptWorkerService;
    private volatile DecryptWorkerService decryptWorkerService;

    private WorkType workType;
    private View viewEmpty;
    private TextView tvStatus;
    private TextView tvWorkerCount;
    private Button btnProceed;
    private RecyclerView recyclerView;
    private WorkerProgressAdapter workerProgressAdapter;
    private List<WorkableFileGroup> workableFileGroups = new ArrayList<>();

    private AsyncTask<Void, Void, Void> myAsyncTask = new AsyncTask<Void, Void, Void>() {
        @Override
        protected void onProgressUpdate(Void... values) {
            tvWorkerCount.setText(String.format("Currently using %d crypto workers", workableFileGroups.size()));
            setWorkableFileGroups(workableFileGroups);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            tvStatus.setText("Finished");
            btnProceed.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (true) {
                if (isCancelled()) break;
                if (workType == WorkType.ENCRYPT && encryptWorkerService != null) {
                    workableFileGroups = encryptWorkerService.getWorkableFileGroups();
                } else if (workType == WorkType.DECRYPT && decryptWorkerService != null) {
                    workableFileGroups = decryptWorkerService.getWorkableFileGroups();
                }

                if (workableFileGroups.size() > 0) {
                    boolean hasOnProgress = false;
                    for (WorkableFileGroup workableFileGroup : workableFileGroups) {
                        if (!workableFileGroup.isFinished() && !workableFileGroup.isCancelled())
                            hasOnProgress = true;
                    }
                    publishProgress();
                    if (!hasOnProgress) {
                        break;
                    }
                }

                try {
                    Thread.currentThread();
                    Thread.sleep(300);
                } catch (Exception ex) {
                }
            }
            return null;
        }
    };


    private void setWorkableFileGroups(List<WorkableFileGroup> workableFileGroups) {
        workerProgressAdapter.setWorkableFileGroups(workableFileGroups);
    }

    private ServiceConnection encryptionServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            EncryptWorkerService.LocalBinder binderBridge = (EncryptWorkerService.LocalBinder) binder;
            encryptWorkerService = binderBridge.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            encryptWorkerService = null;
        }
    };

    private ServiceConnection decryptionServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            DecryptWorkerService.LocalBinder binderBridge = (DecryptWorkerService.LocalBinder) binder;
            decryptWorkerService = binderBridge.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            decryptWorkerService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_progress);

        Intent intent = getIntent();
        workType = (WorkType) intent.getSerializableExtra(EXTRA_WORK_TYPE);

        if (workType == WorkType.ENCRYPT && EncryptWorkerService.isActive()) {
            setTitle("Encrypting..");
            Intent serviceIntent = new Intent(this, EncryptWorkerService.class);
            bindService(serviceIntent, encryptionServiceConnection, Context.BIND_AUTO_CREATE);
        } else if (workType == WorkType.DECRYPT && DecryptWorkerService.isActive()) {
            setTitle("Decrypting..");
            Intent serviceIntent = new Intent(this, DecryptWorkerService.class);
            bindService(serviceIntent, decryptionServiceConnection, Context.BIND_AUTO_CREATE);
        } else if (workType == WorkType.BOTH && EncryptWorkerService.isActive()) {
            setTitle("Re-encrypting..");
        } else {
            finish();
        }

        viewEmpty = findViewById(R.id.view_empty);

        tvStatus = findViewById(R.id.tv_status);
        tvWorkerCount = findViewById(R.id.tv_worker_count);

        btnProceed = findViewById(R.id.btn_proceed);
        btnProceed.setVisibility(View.GONE);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (!sharedPreferences.getBoolean("DrawerActivityActive", false)) {
                    Intent intent = new Intent(getApplicationContext(), DrawerActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        workerProgressAdapter = new WorkerProgressAdapter(getApplicationContext(), viewEmpty);
        recyclerView.setAdapter(workerProgressAdapter);

        myAsyncTask.execute();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            myAsyncTask.cancel(true);
            unbindService(encryptionServiceConnection);
        } catch (Exception ex) {
        }

        try {
            myAsyncTask.cancel(true);
            unbindService(decryptionServiceConnection);
        } catch (Exception ex) {
        }
    }

    public enum WorkType implements Serializable {
        ENCRYPT,
        DECRYPT,
        BOTH
    }
}
