package luvegroup.sdc;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.*;

import java.time.Instant;

public class SyncWorker extends Worker {

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // Perform your sync operation here
            long startTime = Instant.now().getEpochSecond();
            SyncService.isPending = true;
            SyncService.lastUpdate = startTime;
            Log.d("MainActivity", "Starting sync!");
            ContactManager.synchronizeContacts(getApplicationContext());
            long endTime = Instant.now().getEpochSecond();
            Log.d("MainActivity", "Sync finished in: " + (endTime - startTime) + "s!");
            SyncService.isPending = false;
        } catch(Exception e) {
            e.printStackTrace();
        }


        return Result.success();
    }
}
