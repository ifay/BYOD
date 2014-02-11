
package com.byod;

import android.app.Application;
import android.util.Log;

public class BYODApplication extends Application {

    private static BYODApplication sInstance;

    public static BYODApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        sInstance = this;
        Log.d("test","app oncreate");
    }

    
}
