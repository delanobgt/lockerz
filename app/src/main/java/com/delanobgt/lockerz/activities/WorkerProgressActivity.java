package com.delanobgt.lockerz.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.adapters.WorkerProgressAdapter;
import com.delanobgt.lockerz.modules.WorkableFileGroup;
import com.delanobgt.lockerz.services.EncryptWorkerService;

import java.io.Serializable;
import java.util.List;

public class WorkerProgressActivity extends AppCompatActivity {

    public static final String EXTRA_WORK_TYPE = "com.delanobgt.lockerz.activities.EXTRA_WORK_TYPE";

    private volatile EncryptWorkerService encryptWorkerService;
    private WorkType workType;
    private View viewEmpty;
    private Button btnProceed;
    private RecyclerView recyclerView;
    private WorkerProgressAdapter workerProgressAdapter;
    private List<WorkableFileGroup> workableFileGroups;

    private AsyncTask<Void, Void, Void> myAsyncTask = new AsyncTask<Void, Void, Void>() {
        @Override
        protected void onProgressUpdate(Void... values) {
            setWorkableFileGroups(workableFileGroups);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            btnProceed.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (true) {
                if (encryptWorkerService != null) {
                    workableFileGroups = encryptWorkerService.getWorkableFileGroups();
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
        } else if (workType == WorkType.DECRYPT && EncryptWorkerService.isActive()) {
            setTitle("Decrypting..");
        } else if (workType == WorkType.BOTH && EncryptWorkerService.isActive()) {
            setTitle("Re-encrypting..");
        }

        viewEmpty = findViewById(R.id.view_empty);

        btnProceed = findViewById(R.id.btn_proceed);
        btnProceed.setVisibility(View.GONE);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    public enum WorkType implements Serializable {
        ENCRYPT,
        DECRYPT,
        BOTH
    }
}
