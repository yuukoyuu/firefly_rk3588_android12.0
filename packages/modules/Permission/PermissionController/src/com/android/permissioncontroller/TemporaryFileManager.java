package com.android.permissioncontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.android.permissioncontroller.permission.model.AppPermissionGroup;
import com.android.permissioncontroller.permission.model.AppPermissions;
import com.android.permissioncontroller.permission.model.Permission;
import com.android.permissioncontroller.permission.utils.PermissionGrantHelper;
import com.android.permissioncontroller.permission.utils.ArrayUtils;
import com.android.permissioncontroller.permission.utils.Utils;
import java.util.List;

import java.io.File;
import java.io.IOException;

/**
 * Manages files of the package installer and resets state during boot.
 */
public class TemporaryFileManager extends BroadcastReceiver {
    private static final String LOG_TAG = TemporaryFileManager.class.getSimpleName();

    /**
     * Create a new file to hold a staged file.
     *
     * @param context The context of the caller
     *
     * @return A new file
     */
    public static File getStagedFile(Context context) throws IOException {
        return File.createTempFile("package", ".apk", context.getNoBackupFilesDir());
    }

    /**
     * Get the file used to store the results of installs.
     *
     * @param context The context of the caller
     *
     * @return the file used to store the results of installs
     */
    public static File getInstallStateFile( Context context) {
        return new File(context.getNoBackupFilesDir(), "install_results.xml");
    }

    /**
     * Get the file used to store the results of uninstalls.
     *
     * @param context The context of the caller
     *
     * @return the file used to store the results of uninstalls
     */
    public static File getUninstallStateFile( Context context) {
        return new File(context.getNoBackupFilesDir(), "uninstall_results.xml");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("permission", "action==="+ intent.getAction());

        context.startService(new Intent(context, PackageChangedService.class));


        // PermissionGrantHelper.slientGrantRuntimePermission(context, "com.android.dialer");//for system
        // PermissionGrantHelper.slientGrantRuntimePermission(context, "cn.kaer.kecontacts");//for kaer
        // PermissionGrantHelper.slientGrantRuntimePermission(context, "cn.kaer.sipphone");//for kaer
        // PermissionGrantHelper.slientGrantRuntimePermission(context, "com.android.soundrecorder");//for kaer
        // PermissionGrantHelper.slientGrantRuntimePermission(context, "com.kaer.weatherwidget");//for kaer

        long systemBootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime();

        File[] filesOnBoot = context.getNoBackupFilesDir().listFiles();

        if (filesOnBoot == null) {
            return;
        }

        for (int i = 0; i < filesOnBoot.length; i++) {
            File fileOnBoot = filesOnBoot[i];

            if (systemBootTime > fileOnBoot.lastModified()) {
                boolean wasDeleted = fileOnBoot.delete();
                if (!wasDeleted) {
                    Log.w(LOG_TAG, "Could not delete " + fileOnBoot.getName() + " onBoot");
                }
            } else {
                Log.w(LOG_TAG, fileOnBoot.getName() + " was created before onBoot broadcast was "
                        + "received");
            }
        }
    }
}