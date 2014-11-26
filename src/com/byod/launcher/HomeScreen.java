/**
 *
 */
package com.byod.launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.byod.AuthenticateActivity;
import com.byod.BYODActivity;
import com.byod.BYODApplication;
import com.byod.R;

/**
 * @author ifay
 */

public class HomeScreen extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        if (!getIntent().getBooleanExtra("isLoggedIn", false) && !BYODApplication.loggedIn) {
            Intent intent = new Intent(this, AuthenticateActivity.class);
            startActivityForResult(intent, BYODActivity.REQUEST_AUTH_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_CANCELED) {
            this.finish();//http://smallwoniu.blog.51cto.com/3911954/1248643 应用退出方法
            return;
        }

        if (requestCode == BYODActivity.REQUEST_AUTH_CODE) {
            BYODApplication.loggedIn = data.getBooleanExtra("AuthResult", false);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home_screen, menu);
//        return true;
//    }
}
