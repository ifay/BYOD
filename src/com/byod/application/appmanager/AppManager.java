package com.byod.application.appmanager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.byod.BYODApplication;
import com.byod.application.perm.PermConstants;

import java.util.ArrayList;
import java.util.Arrays;

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
     * @return
     * @author yyf
     */
    public ArrayList<PackageInfo> getInstalledApplications() {
        // mpm.getInstalledApplications(PackageManager.GET_DISABLED_COMPONENTS);
        ArrayList<PackageInfo> list = (ArrayList<PackageInfo>) mPm
                .getInstalledPackages(PackageManager.GET_DISABLED_COMPONENTS);
        return list;
    }

    /**
     * get Sensitive Application TODO 区分系统应用？？？还是设定应用白名单
     * takes time,run in thread
     *
     * @param force:whether use enterprise security policies
     * @return sensitive applications 'PackageInfo'
     * @author yyf
     * @see PermConstants#PERMS_SENSTIVE
     */
    public ArrayList<PackageInfo> getSensitiveApplications(boolean force) {
        ArrayList<PackageInfo> list = (ArrayList<PackageInfo>) mPm
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);
        ArrayList<PackageInfo> list2 = new ArrayList<PackageInfo>();
        for (PackageInfo pi : list) {
            int flag = pi.applicationInfo.flags;
            // 过滤系统应用
            if (!force) {
                if (((flag & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM)
                        || ((flag & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) {
                    Log.d("test is sys", "current pkg:" + pi.packageName);
                    continue;
                }
            }
            // 过滤白名单应用
            if (force) {
                //TODO 拉取服务器数据
                //分析服务器数据
            }
            // 下句获得的是<permissions>项中的权限
            // PermissionInfo[] permInfoList = pi.permissions;

            //<uses-permission>的权限
            String[] permInfoList = pi.requestedPermissions;
            if (permInfoList == null)
                continue;
            for (String permName : permInfoList) {
                if (Arrays.binarySearch(PermConstants.PERMS_SENSTIVE, permName) >= 0) {
                    list2.add(pi);
                    Log.d("senstive", pi.packageName + ":" + permName);
                    break;
                }
            }
        }
        return list2;
    }
}
