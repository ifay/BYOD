/**
 * 
 */
package com.byod;

import com.byod.utils.CommonUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

/**
 * @author ifay
 *
 */
public abstract class BYODActivity extends Activity {

    public static boolean loggedIn = false;
    public static int REQUEST_AUTH_CODE = 1;
    public ExitListenerReceiver exitre;

    public abstract void onCreate();
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreate();
        regListener();
    }
    
    /**
     * 注册退出事件监听
     */
    private void regListener() {
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

    //每个activity开始的时候都跳转验证界面
    @Override
    protected void onResume() {
        super.onResume();
        if (!loggedIn) {
            Intent intent = new Intent(this, AuthenticateActivity.class);
            startActivityForResult(intent, REQUEST_AUTH_CODE);
        }
    }


    //当界面不再在前台时清除登录数据
    @Override
    protected void onStop() {
        super.onStop();
        loggedIn = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return ;
        }

        if (requestCode == REQUEST_AUTH_CODE) {
            loggedIn = data.getBooleanExtra("AuthResult", false);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loggedIn = false;
        unRegListener(exitre);
    }
}
