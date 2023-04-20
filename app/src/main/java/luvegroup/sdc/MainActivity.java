package luvegroup.sdc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public PermissionManager permissionManager;
    public static ContactManager contactManager;
    public static PowerManager powerManager;

    public static Context context;

    public static String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        packageName = getPackageName();
        contactManager = new ContactManager(context);
        permissionManager = new PermissionManager(this);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        //showBatteryOptimization();

        //showAutostartSettings();




        Button buttonOpenManagementActivity = findViewById(R.id.manageButton);
        buttonOpenManagementActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the ManagementActivity
                Intent intent = new Intent(MainActivity.this, ManagementActivity.class);
                startActivity(intent);
            }
        });

        Button buttonSettings = findViewById(R.id.settingsButton);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettings();
            }
        });

        Button buttonSynchronizeContacts = findViewById(R.id.synchronizeButton);
        buttonSynchronizeContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ContactManager.synchronizeContacts(getApplicationContext());
                SyncService.syncContactsNow();
            }
        });

        TextView lastUpdateText = findViewById(R.id.lastUpdateText);

        // Create a Handler
        Handler handler = new Handler(Looper.getMainLooper());

        // Define a Runnable for updating the lastUpdateText
        Runnable updateLastUpdateText = new Runnable() {
            @Override
            public void run() {
                // Get the current date and time
                Date oldDate = new Date(SyncService.lastUpdate * 1000L);

                // Create a date format
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                // Format the date
                String humanReadableDate = sdf.format(oldDate);
                if (SyncService.isPending) {
                    lastUpdateText.setText("Trwa synchronizacja...");

                } else {
                    lastUpdateText.setText("Ostatnia synchronizacja: " + humanReadableDate);
                }

                buttonSynchronizeContacts.setEnabled(!SyncService.isPending);

                // Schedule the Runnable to run again after 1 second
                handler.postDelayed(this, 1000);

            }
        };

        // Start the Runnable initially
        handler.post(updateLastUpdateText);
        /*

        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(this, MyDeviceAdminReceiver.class);
        if (dpm.isDeviceOwnerApp(getPackageName())) {
            // Your app is already a device owner app
        } else {
            // Your app is not a device owner app, show system intent to enable device owner mode
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable device owner mode for my app");
            startActivityForResult(intent, REQUEST_CODE_ENABLE_DEVICE_OWNER);
        }
*/


        }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static void continuePermissionsGranted() {
        Log.d("MainActivity", "Permissions granted, continuing...");

        startSyncServiceIfNeeded();
        if (!Settings.canDrawOverlays(context)) {
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            Uri uri = Uri.fromParts("package", packageName, null);

            myIntent.setData(uri);
            context.startActivity(myIntent);
        }

    }

    public static void startSyncServiceIfNeeded() {
        if (!isSyncServiceRunning()) {
            Intent intent = new Intent(context, SyncService.class);
            Log.d("MainActivity", "Starting service...");
            context.startForegroundService(intent);
        }
    }

    private static boolean isSyncServiceRunning() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SyncService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public void showAutostartSettings() {
        Intent intent = new Intent();
        String manufacturer = android.os.Build.MANUFACTURER.toLowerCase(Locale.getDefault());
        switch (manufacturer) {
            case "xiaomi":
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                break;
            case "oppo":
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                break;
            case "vivo":
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                break;
            case "letv":
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
                break;
            case "honor":
            case "huawei":
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"));
                break;
            default:
                intent.setAction(Settings.ACTION_SETTINGS);
                break;
        }
        startActivity(intent);
    }

    /*
    public void showStartupList() {
        try {
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity");
            intent.setComponent(componentName);
            startActivity(intent);
        } catch (Exception e) {
            try {
                e.printStackTrace();

                Intent intent = new Intent();
                ComponentName componentName = new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity");
                intent.setComponent(componentName);
                startActivity(intent);
            } catch (Exception e2) {
                e2.printStackTrace();

                Intent intent = new Intent();
                ComponentName componentName = new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity");
                intent.setComponent(componentName);
                startActivity(intent);
            }
        }
    }

     */

    public void showSettings() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
    }


}