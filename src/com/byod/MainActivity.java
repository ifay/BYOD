package com.byod;

import com.byod.device.DeviceUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView tv = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        tv.setText(DeviceUtils.getInstance(this).getIMEI()+"\n"+
                DeviceUtils.getInstance(this).getIMSI()+"\n"+
                DeviceUtils.getInstance(this).getTEL());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        tv = (TextView)findViewById(R.id.textview);
    }
}
