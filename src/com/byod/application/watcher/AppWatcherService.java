package com.byod.application.watcher;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/*
 * 监控当前正在运行的应用
 */
public class AppWatcherService extends Service {

    private static final String TAG = "AppWatcherService";
    private Handler mHandler;
    private ActivityManager mActivityManager;
    private MyBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {
        public List<RunningServiceInfo> getRunningServices(){
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
        mActivityManager = (ActivityManager)this.getSystemService("activity");
        ComponentName topActivity = mActivityManager.getRunningTasks(1).get(0).topActivity;
        List <RunningServiceInfo> serviceList= mActivityManager.getRunningServices(100);
        Log.d("test",topActivity.getPackageName());
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
