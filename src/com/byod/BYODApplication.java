
package com.byod;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.byod.utils.CommonUtils;

public class BYODApplication extends Application {

    private static BYODApplication sInstance;
    public static boolean loggedIn = false;
    public static int REQUEST_AUTH_CODE = 1;
    public ExitListenerReceiver exitre;

    public static BYODApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        regExitListener();
        //******TODO *********
        //Notification
        //http://www.oschina.net/question/234345_40111
    }

    /**
     * 注册退出事件监听
     */
    private void regExitListener() {
        exitre = new ExitListenerReceiver();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(this.getPackageName() + "."
                             + CommonUtils.ExitListenerReceiver);
        this.registerReceiver(exitre, intentfilter);
     }

    private void unRegListener( ExitListenerReceiver receiver) {
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }
    class ExitListenerReceiver extends BroadcastReceiver {
            @Override
             public void onReceive(Context context, Intent i) {
                ((Activity) context).finish();
            }
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        loggedIn = false;
        unRegListener(exitre);
    }
    
}
