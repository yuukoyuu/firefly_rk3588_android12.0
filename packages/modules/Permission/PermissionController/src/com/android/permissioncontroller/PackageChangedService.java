package com.android.permissioncontroller;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.net.Uri;
import com.android.permissioncontroller.permission.utils.PermissionGrantHelper;

public class PackageChangedService extends Service {

    private final String TAG = "permission";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate OK");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand OK");
        packageChangedBroadcastReceiver = new PackageChangedBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        registerReceiver(packageChangedBroadcastReceiver, intentFilter);

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        try{
            unregisterReceiver(packageChangedBroadcastReceiver);
        }catch(Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }



    private PackageChangedBroadcastReceiver packageChangedBroadcastReceiver;

    private class PackageChangedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                String action = intent.getAction();
                String packageName = intent.getData().getSchemeSpecificPart();
                Log.e(TAG, "PackageChangedBroadcastReceiver action==" + action);
                Log.i(TAG, "PackageChangedBroadcastReceiver packageName==" + packageName);

                if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                    PermissionGrantHelper.slientGrantRuntimePermission(context, packageName);

                } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {

                } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
                    Intent ccIntent = new Intent();
                    ccIntent.setAction("android.intent.action.MY_PACKAGE_REPLACED");
                    ccIntent.setData(Uri.parse("package:" + packageName));
                    ccIntent.addFlags(0x01000000);
                    context.sendBroadcast(ccIntent);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}