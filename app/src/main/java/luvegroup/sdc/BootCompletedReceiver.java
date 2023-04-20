package luvegroup.sdc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/*
public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, SyncService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }
}

 */

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MainActivity", "Boot detected!");
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
//            Intent n =  context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            //            n.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
            //    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //            context.startActivity(n);

            Intent myIntent = new Intent(context, MainActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myIntent);
        }
    }
}