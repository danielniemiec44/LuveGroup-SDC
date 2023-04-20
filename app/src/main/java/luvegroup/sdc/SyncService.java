package luvegroup.sdc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SyncService extends Service {
    public static Timer timer;
    private static final String CHANNEL_ID = "SDCChannel";
    private static final String CHANNEL_NAME = "SDCSync";
    private static final int FOREGROUND_SERVICE_ID = 2137;
    public static Context context;

    public static long lastUpdate;
    public static boolean isPending;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;
        startForegroundService();

        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(SyncWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("SyncContacts", ExistingPeriodicWorkPolicy.UPDATE, syncRequest);


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startForegroundService() {
        // Create a notification channel (required on Android 8.0 and higher)
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        // Create a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                //.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("SyncService")
                .setContentText("Syncing contacts")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Start the service in the foreground with the notification
        startForeground(FOREGROUND_SERVICE_ID, builder.build());


    }

    public static void syncContactsNow() {
        OneTimeWorkRequest syncRequest = new OneTimeWorkRequest.Builder(SyncWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build();
        WorkManager.getInstance(context).enqueue(syncRequest);
    }







}