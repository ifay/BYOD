package com.byod.application.watcher;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

/*
 * 监控当前正在运行的应用,if byod is not foreground, throw a notifier to switch
 * if white list application is no longer installed, throw a notifier? exit? wipe?
 */
public class AppWatcherService extends Service {

    private static final String TAG = "AppWatcherService";
    private Handler mHandler;
    private ActivityManager mActivityManager;
    private MyBinder mBinder = new MyBinder();
    private static int sMaxAppsNum = 1000;

    public class MyBinder extends Binder {
        public List<RunningServiceInfo> getRunningServices() {
            return null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mActivityManager = (ActivityManager) this.getSystemService("activity");
        ComponentName topActivity = mActivityManager.getRunningTasks(1).get(0).topActivity;
        //获得所有在运行的服务
        List<RunningServiceInfo> serviceList = mActivityManager.getRunningServices(sMaxAppsNum);
        Log.d("test", topActivity.getPackageName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

}
