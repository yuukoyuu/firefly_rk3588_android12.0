package com.android.permissioncontroller.permission.utils;

import android.content.Context;
import android.util.Log;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.android.permissioncontroller.permission.model.AppPermissionGroup;
import com.android.permissioncontroller.permission.model.AppPermissions;
import com.android.permissioncontroller.permission.model.Permission;
import com.android.permissioncontroller.permission.utils.ArrayUtils;
import com.android.permissioncontroller.permission.utils.Utils;
import java.util.List;


public class PermissionGrantHelper{

    public static void slientGrantRuntimePermission(Context context, String packageName){
        PackageInfo packageInfo;

        try {
            packageInfo =  context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("permission", "can't get PackageInfo for packageName="+ packageName);
            return;
        }
        
       AppPermissions  mAppPermissions = new AppPermissions(context, packageInfo, false, true,
                new Runnable() {
                    @Override
                    public void run() {
                        
                    }
                });

       Log.e("permission", " AppPermissionGroup size=="+mAppPermissions.getPermissionGroups().size());
       if (mAppPermissions.getPermissionGroups().isEmpty()) {
            Log.e("permission", "mAppPermissions size isEmpty");
            return;
        }
        for (AppPermissionGroup group : mAppPermissions.getPermissionGroups()) {
            String[] permissionsToGrant = null;
            final int permissionCount = group.getPermissions().size();
            //Log.e("permission", "permissionCount:"+permissionCount);
            for (int j = 0; j < permissionCount; j++) {
                final Permission permission = group.getPermissions().get(j);
                //if (!permission.isGranted()) {
                    permissionsToGrant = ArrayUtils.appendString(
                            permissionsToGrant, permission.getName());
                     //Log.e("permission", "permissionName=" + permission.getName());
                //}
            }
            if (permissionsToGrant != null) {
                group.grantRuntimePermissions(true,false, permissionsToGrant);
                Log.i("permission", "grantRuntimePermissions permissionsToGrant");
                //group.revokeRuntimePermissions(false);
            }
            //group.resetReviewRequired();
        }

         mAppPermissions.persistChanges(true);
    }
	
}