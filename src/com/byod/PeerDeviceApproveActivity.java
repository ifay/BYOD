/**
 * 
 */
package com.byod;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.byod.R;
import com.byod.utils.DeviceUtils;

/**
 * @author ifay
 *
 */
public class PeerDeviceApproveActivity extends Activity {
    private PeerDeviceApproveActivity mActivity = null;
    private TextView messageTV = null;
    private Button approveButton = null;
    private Button disApproveButton = null;
    private Intent i = null;
    private String[] deviceInfo = null; //[0]deviceID,[1]deviceName

    private OnClickListener clickListener = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            if (v.equals((Button)approveButton)) {
                try {
                    DeviceUtils.approvePeerDevice(deviceInfo[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (v.equals((Button)disApproveButton)) {
                try { 
                    DeviceUtils.disapproveDevice(deviceInfo[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mActivity.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peer_message);
        mActivity = this;
        messageTV = (TextView) findViewById(R.id.message);
        approveButton = (Button) findViewById(R.id.approve);
        disApproveButton = (Button) findViewById(R.id.disapprove);
        approveButton.setOnClickListener(clickListener );
        disApproveButton.setOnClickListener(clickListener);
        
        //clear notification
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();

        i = getIntent();
        deviceInfo = i.getExtras().getStringArray("DeviceInfo");
        StringBuffer text = new StringBuffer();
        text.append("是否允许设备 ");
        text.append(deviceInfo[1]);
        text.append(" 注册");
        messageTV.setText(text);
    }
    
}
