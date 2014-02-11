
package com.byod;

import com.byod.application.appmanager.AppManager;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView tv = null;
    private ListView lv = null;

    // test------
    private ActivityManager mActivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        // tv.setText(DeviceUtils.getInstance(this).getIMEI()+"\n"+
        // DeviceUtils.getInstance(this).getIMSI()+"\n"+
        // DeviceUtils.getInstance(this).getTEL());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textview);
        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(new ArrayAdapter<String>(BYODApplication.getInstance(),
                android.R.layout.simple_expandable_list_item_1,
                AppManager.getInstance().getSensitiveApplications()));
    }

    // test-------
    private void testSth() {
        // mActivityManager =
        // (ActivityManager)this.getSystemService("activity");
        // ComponentName topActivity =
        // mActivityManager.getRunningTasks(1).get(0).topActivity;
        // Log.d("test",topActivity.getPackageName());
        // tv.setText(topActivity.getPackageName());

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d("test", "onDestroy");
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.d("test", "onDestroy");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.d("test", "onResume");
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.d("test", "onStart");
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Log.d("test", "onStop");
    }
}
