
package com.byod.application.appmanager;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.util.Log;

import com.byod.BYODApplication;
import com.byod.application.perm.PermConstants;

public class AppManager {

    private Context mContext;
    private static AppManager sInstance;
    private static PackageManager mPm;

    public static AppManager getInstance() {
        if (sInstance == null) {
            sInstance = new AppManager();
        }
        return sInstance;
    }

    private AppManager() {
        // TODO init works
        mContext = BYODApplication.getInstance();
        mPm = mContext.getPackageManager();
    }

    public PackageManager getPackageManager() {
        return mPm;
    }

    /**
     * get all installed apps exclude disabled
     * 
     * @author yyf
     * @return
     */
    public ArrayList<PackageInfo> getInstalledApplications() {
        // mpm.getInstalledApplications(PackageManager.GET_DISABLED_COMPONENTS);
        ArrayList<PackageInfo> list = (ArrayList<PackageInfo>) mPm
                .getInstalledPackages(PackageManager.GET_DISABLED_COMPONENTS);
        return list;
    }

    /**
     * get Sensitive Application TODO 区分系统应用？？？还是设定应用白名单
     * 
     * @author yyf
     * @return sensitive applications packageneame
     * @see PermConstants#PERMS_SENSTIVE
     */
    public ArrayList<String> getSensitiveApplications() {
        ArrayList<PackageInfo> list = (ArrayList<PackageInfo>) mPm
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);
        ArrayList<String> list2 = new ArrayList<String>();
        for (PackageInfo pi : list) {
            // 过滤系统应用 TODO 应用白名单
            int flag = pi.applicationInfo.flags;
            if (((flag & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM)
                    || ((flag & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) {
                Log.d("test is sys", "current pkg:" + pi.packageName);
                continue;
            }

            // 下句获得的是<permissions>项中的权限
            // PermissionInfo[] permInfoList = pi.permissions;
            String[] permInfoList = pi.requestedPermissions;
            if (permInfoList == null)
                continue;
            for (String permName : permInfoList) {
                if (Arrays.binarySearch(PermConstants.PERMS_SENSTIVE, permName) >= 0) {
                    list2.add(pi.packageName);
                    Log.d("senstive", pi.packageName + ":" + permName);
                    break;
                }
            }
        }
        return list2;
    }
}
