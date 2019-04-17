package com.delanobgt.lockerz.services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.activities.DrawerActivity;
import com.delanobgt.lockerz.modules.FileGroup;
import com.delanobgt.lockerz.modules.FileModifier;
import com.delanobgt.lockerz.modules.FileWorm;
import com.delanobgt.lockerz.modules.VigenereFileModifier;
import com.delanobgt.lockerz.modules.WorkableFileGroup;
import com.delanobgt.lockerz.room.entities.FileItem;
import com.delanobgt.lockerz.room.entities.Locker;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EncryptWorkerService extends Service {

    public static final String NOTIFICATION_CHANNEL_ID = "com.delanobgt.lockerz.serviceChannel";
    public static final int NOTIFICATION_ID = 1;
    public static final int PENDING_INTENT_REQUEST = 2;

    public static final String EXTRA_PASSWORD = "com.delanobgt.lockerz.services.EXTRA_PASSWORD";
    public static final String EXTRA_LOCKER = "com.delanobgt.lockerz.services.EXTRA_LOCKER";
    public static final String EXTRA_FILE_ITEMS = "com.delanobgt.lockerz.services.EXTRA_FILE_ITEMS";

    private static volatile boolean active = false;
    private final LocalBinder localBinder = new LocalBinder();
    private List<FileItem> fileItems;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private AsyncTask<Void, Void, Void>[] asyncTasks;
    private WorkableFileGroup[] workableFileGroups = new WorkableFileGroup[0];
    private FileModifier fileModifier;

    public static boolean isActive() {
        return active;
    }

    public List<WorkableFileGroup> getWorkableFileGroups() {
        return new ArrayList<>(Arrays.asList(workableFileGroups));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = createNotificationManagerWithChannel();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public int onStartCommand(final Intent intent, final int flags, int startId) {
        active = true;

        Intent notificationIntent = new Intent(this, DrawerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                PENDING_INTENT_REQUEST, notificationIntent, 0);

        notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Encryption in progress")
                .setContentText("Indexing files..")
                .setSmallIcon(R.drawable.ic_lock_green_24dp)
                .setContentIntent(pendingIntent)
                .setProgress(0, 0, true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final int pWorkerCount = Integer.parseInt(sharedPreferences.getString("workerCount", "3"));

        startForeground(NOTIFICATION_ID, notificationBuilder.build());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Locker locker = (Locker) intent.getSerializableExtra(EXTRA_LOCKER);
                fileItems = (ArrayList<FileItem>) intent.getSerializableExtra(EXTRA_FILE_ITEMS);
                String password = intent.getStringExtra(EXTRA_PASSWORD);

                if (locker.getEncryptionType() == Locker.EncryptionType.CAESAR) {
                    fileModifier = new VigenereFileModifier(password, true);
                } else if (locker.getEncryptionType() == Locker.EncryptionType.VIGENERE) {
                    fileModifier = new VigenereFileModifier(password, true);
                } else if (locker.getEncryptionType() == Locker.EncryptionType.XOR) {
                    fileModifier = new VigenereFileModifier(password, true);
                }

                List<File> flattenedFiles = FileWorm.flattenFiles(FileWorm.convertFileItemsToFiles(fileItems));
                int workerCount = Math.min(pWorkerCount, flattenedFiles.size());

                List<FileGroup> fileGroups = FileWorm.divideIntoFileGroups(flattenedFiles, workerCount);
                workableFileGroups = new WorkableFileGroup[workerCount];
                for (int i = 0; i < workerCount; i++) {
                    workableFileGroups[i] = new WorkableFileGroup(fileGroups.get(i), fileModifier);
                }

                asyncTasks = new AsyncTask[workerCount];
                for (int i = 0; i < workerCount; i++) {
                    final int index = i;
                    final WorkableFileGroup workableFileGroup = workableFileGroups[index];
                    asyncTasks[i] = new SingleWorkerAsyncTask(workableFileGroup);
                }
                executeAllAsyncTasks();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (workableFileGroups != null && workableFileGroups.length == 0) {
                    active = false;
                    Toast.makeText(getApplicationContext(), "No file(s) found. Aborting operation.", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    private void executeAllAsyncTasks() {
        for (int i = 0; i < asyncTasks.length; i++) {
            asyncTasks[i].executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void updateServiceState() {
        int successCount = 0, failCount = 0, onProgressCount = 0, totalCount = 0;
        long totalModifiedBytes = 0, totalOriginalBytes = 0;
        for (WorkableFileGroup workableFileGroup : workableFileGroups) {
            totalCount += workableFileGroup.getOriginalFilesCount();
            totalModifiedBytes = workableFileGroup.getOriginalTotalBytes();
            if (workableFileGroup.isFinished()) {
                successCount += workableFileGroup.getOriginalFilesCount();
                totalModifiedBytes += workableFileGroup.getOriginalTotalBytes();
            } else if (workableFileGroup.isCancelled()) {
                failCount += workableFileGroup.getOriginalFilesCount();
                totalModifiedBytes += workableFileGroup.getOriginalTotalBytes();
            } else {
                onProgressCount += workableFileGroup.getModifiedFilesCount();
                totalModifiedBytes += workableFileGroup.getModifiedTotalBytes();
            }
        }

        if (totalCount == successCount + failCount) {
            active = false;
            stopSelf();
            notificationBuilder
                    .setProgress(0, 0, false)
                    .setContentTitle("Encryption done")
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setContentText(String.format("Total: %d, Success: %d, Fail: %d", totalCount, successCount, failCount));
        } else {
//            int intTotalModifiedBytes = (int) (((double) totalModifiedBytes / Long.MAX_VALUE) * Integer.MAX_VALUE);
//            int intTotalOriginalBytes = (int) (((double) totalOriginalBytes / Long.MAX_VALUE) * Integer.MAX_VALUE);
//            notificationBuilder
//                    .setProgress(intTotalOriginalBytes, intTotalModifiedBytes, false)
//                    .setContentTitle("Encryption in progress")
//                    .setContentText(String.format("Processed %d out of %d file(s)", successCount + failCount, totalCount));

            notificationBuilder
                    .setProgress(totalCount, successCount + failCount, false)
                    .setContentTitle("Encryption in progress")
                    .setContentText(String.format("Processed %d out of %d file(s)", successCount + failCount, totalCount));
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private NotificationManager createNotificationManagerWithChannel() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "CryptWorkerServiceChannel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(serviceChannel);
        }
        return notificationManager;
    }

    public class LocalBinder extends Binder {
        public EncryptWorkerService getService() {
            return EncryptWorkerService.this;
        }
    }

    private class SingleWorkerAsyncTask extends AsyncTask<Void, Void, Void> {

        private WorkableFileGroup workableFileGroup;

        public SingleWorkerAsyncTask(WorkableFileGroup workableFileGroup) {
            this.workableFileGroup = workableFileGroup;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (!workableFileGroup.open()) {
                Log.e("HAHAHA", "OPEN ERROR ON " + workableFileGroup.toString());
                cancel(true);
                workableFileGroup.setCancelled();
                updateServiceState();
                return null;
            }
            while (true) {
                if (isCancelled()) {
                    break;
                } else {
                    if (workableFileGroup.hasNext()) {
                        Log.e("HAHAHA", workableFileGroup.getOriginalTotalBytes() + "");
                        if (!workableFileGroup.travelNext()) {
                            Log.e("HAHAHA", "TRAVEL ERROR ON " + workableFileGroup.toString());
                            cancel(true);
                            workableFileGroup.setCancelled();
                            break;
                        }
                        updateServiceState();
                    } else {
                        break;
                    }
                }
            }
            if (!workableFileGroup.close()) {
                Log.e("HAHAHA", "CLOSE ERROR ON " + workableFileGroup.toString());
                cancel(true);
                workableFileGroup.setCancelled();
            } else {
                workableFileGroup.setFinished();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            workableFileGroup.deleteOriginalFiles();
            updateServiceState();
        }

        @Override
        protected void onCancelled() {
            workableFileGroup.close();
            workableFileGroup.deleteModifiedFiles();
            updateServiceState();
        }
    }

}