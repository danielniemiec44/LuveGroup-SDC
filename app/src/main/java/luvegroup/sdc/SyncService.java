package luvegroup.sdc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class SyncService extends Service {
    private Timer timer;
    private static final String CHANNEL_ID = "SDCChannel";
    private static final String CHANNEL_NAME = "SDCSync";
    private static final int FOREGROUND_SERVICE_ID = 2137;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();
        startPeriodicSync();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPeriodicSync();
    }

    private void startPeriodicSync() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ContactManager.synchronizeContacts(SyncService.this);
            }
        }, 0, 3600000); // Run the task every 60 seconds
    }

    private void stopPeriodicSync() {
        if (timer != null) {
            timer.cancel();
        }
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



}