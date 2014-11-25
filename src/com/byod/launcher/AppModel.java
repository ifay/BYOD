package com.byod.launcher;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.io.File;

/**
 * @credit http://developer.android.com/reference/android/content/AsyncTaskLoader.html
 */
public class AppModel {

    private final Context mContext;
    private final ApplicationInfo mInfo;

    private String mAppLabel;
    private Drawable mIcon;

    private boolean mMounted;
    private final File mApkFile;

    public AppModel(Context context, ApplicationInfo info) {
        mContext = context.getApplicationContext();
        mInfo = info;
        if (null != info) {
            mApkFile = new File(info.sourceDir);
        } else {
            mApkFile = null;
        }
    }

    public AppModel(Context context, String lable, Drawable icon) {
        mContext = context.getApplicationContext();
        mInfo = null;
        mApkFile = null;
        mAppLabel = lable;
        mIcon = icon;
    }

    public ApplicationInfo getAppInfo() {
        return mInfo;
    }

    public String getApplicationPackageName() {
        if (null != mInfo) {
            return mInfo.packageName;
        } else {
            return null;
        }

    }

    public String getLabel() {
        if (null != mInfo && null != mApkFile) {
            if (TextUtils.isEmpty(mAppLabel) || !mMounted) {
                if (!mApkFile.exists()) {
                    mMounted = false;
                    mAppLabel = mInfo.packageName;
                } else {
                    mMounted = true;
                    CharSequence label = mInfo.loadLabel(mContext.getPackageManager());
                    mAppLabel = label != null ? label.toString() : mInfo.packageName;
                }
            }
        }
        return mAppLabel;
    }

    public Drawable getIcon() {
        if (null != mInfo && null != mApkFile) {
            if (mIcon == null) {
                if (mApkFile.exists()) {
                    mIcon = mInfo.loadIcon(mContext.getPackageManager());
                } else {
                    mMounted = false;
                }
            } else if (!mMounted) {
                // If the app wasn't mounted but is now mounted, reload
                // its icon.
                if (mApkFile.exists()) {
                    mMounted = true;
                    mIcon = mInfo.loadIcon(mContext.getPackageManager());
                }
            }
        }
        if (null == mIcon) {
            return mContext.getResources().getDrawable(android.R.drawable.sym_def_app_icon);
        }
        return mIcon;
    }
}
