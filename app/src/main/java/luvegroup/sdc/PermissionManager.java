package luvegroup.sdc;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

@RequiresApi(api = Build.VERSION_CODES.P)
public class PermissionManager {


    Activity activity;
    Context context;

    public PermissionManager(Activity activity) {

        this.activity = activity;
        this.context = activity.getApplicationContext();
        checkPermissions();
    }


    private int currentPermissionIndex = 0;
    private String[] requiredPermissions = {
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.FOREGROUND_SERVICE
    };

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1234) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                currentPermissionIndex++;
                checkPermissions();
            } else {
                String message = "Brakujące uprawnienia";
                if(currentPermissionIndex == 0) {
                    message = "Aby aplikacja mogła działać musisz przyznać jej uprawnienia do zapisu kontaktów";
                } else if(currentPermissionIndex == 1) {
                    message = "Aby aplikacja mogła działać musisz przyznać jej uprawnienia do odczytu kontaktów";
                } else if(currentPermissionIndex == 2) {
                    message = "Aby aplikacja mogła działać musisz przyznać jej uprawnienia do korzystania z internetu";
                } else if(currentPermissionIndex == 3) {
                    message = "Aby aplikacja mogła działać musisz przyznać jej uprawnienia do odczytu stanu połączenia z internetem";
                } else if(currentPermissionIndex == 4) {
                    message = "Aby aplikacja mogła działać musisz przyznać jej uprawnienia do działania w tle";
                }
                showAlertPermissionNeeded(message);
            }
        }
    }


    public void checkPermissions() {
        if (currentPermissionIndex < requiredPermissions.length) {
            Log.d("MainActivity", "Activity: " + activity.toString());
            ActivityCompat.requestPermissions(activity, new String[]{requiredPermissions[currentPermissionIndex]}, 1234);
        } else {
            MainActivity.continuePermissionsGranted(context);
        }
    }


    public void showAlertPermissionNeeded(String message) {
        // Explain why the permissions are needed
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Potrzebne uprawnienia aplikacji!");
        builder.setMessage(message);
        builder.setPositiveButton("Przyznaj uprawnienia", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Request permissions
                checkPermissions();
            }
        });
        builder.setNegativeButton("Wyjdź", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Close the application
                activity.finish();
            }
        });
        builder.show();
    }

}
