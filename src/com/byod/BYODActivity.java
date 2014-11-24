/**
 * 
 */
package com.byod;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * @author ifay
 *
 */
public abstract class BYODActivity extends FragmentActivity {

    public static int REQUEST_AUTH_CODE = 1;


    public abstract void onCreate();
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreate();
    }

    //每个activity开始的时候都判断是否需要验证
    @Override
    protected void onResume() {
        super.onResume();
        if (!BYODApplication.loggedIn) {
            Intent intent = new Intent(this, AuthenticateActivity.class);
            startActivityForResult(intent, REQUEST_AUTH_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != FragmentActivity.RESULT_OK) {
            return ;
        }

        if (requestCode == REQUEST_AUTH_CODE) {
            BYODApplication.loggedIn = data.getBooleanExtra("AuthResult", false);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
