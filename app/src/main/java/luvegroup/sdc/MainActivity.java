package luvegroup.sdc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public PermissionManager permissionManager;
    public static ContactManager contactManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactManager = new ContactManager(this);
        permissionManager = new PermissionManager(this);

        Button buttonOpenManagementActivity = findViewById(R.id.manageButton);
        buttonOpenManagementActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the ManagementActivity
                Intent intent = new Intent(MainActivity.this, ManagementActivity.class);
                startActivity(intent);
            }
        });

        Button buttonSynchronizeContacts = findViewById(R.id.synchronizeButton);
        buttonSynchronizeContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactManager.synchronizeContacts(getApplicationContext());
            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static void continuePermissionsGranted(Context context) {
        Log.d("MainActivity", "Permissions granted, continuing...");
        startSyncServiceIfNeeded(context);
        //contactManager.addContactToBook(new Contact("xxx", "yyyy", "1234"));
    }

    public static void startSyncServiceIfNeeded(Context context) {
        if (!isSyncServiceRunning(context)) {
            Intent intent = new Intent(context, SyncService.class);
            Log.d("MainActivity", "Starting service...");
            context.startForegroundService(intent);
        }
    }

    private static boolean isSyncServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SyncService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}