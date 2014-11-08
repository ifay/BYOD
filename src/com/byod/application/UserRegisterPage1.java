
package com.byod.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byod.R;
import com.byod.utils.CommonUtils;
import com.byod.utils.DeviceUtils;
import com.byod.utils.PolicyUtils;

/**
 * @author ifay 完善注册页面，（调用条件：首次使用设备登录） 尝试web View方式实现
 *         步骤一（服务器端）：管理员在UIA上录入用户的基本信息，用户获得默认权限-最低的权限 
 *         步骤二：Page1 输入用户名(可考虑做成翻页形式，“下一步”) 1-2-3 或者 1  
 *         2.1 检查用户名下是否已有设备，若有，则直接增添设备，若无则进入完善注册页面（Page2）
 *         2.2 若确定添加设备|完善注册，将设备信息发送至服务器进行检查。否则退出---在Page2开始，若未同步策略则进行同步。 2.3
 *         同步默认安全策略到本地，并进行检查 2.4 Page4 注册设备 2.5 Page2 完善用户详细信息 2.6 Page3
 *         完善挑战问题答案库 (若是仅增添设备则无需进行此步) 步骤三：设备绑定 TODO
 *         考虑把登陆INFO的方法做成服务？？还是直接用WebView展示？
 */

public class UserRegisterPage1 extends Activity {

    private static final String LOG_TAG = "UserRegisterPage1";

    private static final int MSG_NO_DEVICE = 1000; // 用户名下没有设备
    private static final int MSG_HAVE_DEVICE = 1001;

    private UserRegisterPage1 mActivity;
    private EditText userAccountET = null;
    private Button nextBt = null;
    private String userAccount = "";
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            Intent i;
            switch (what) {
                case MSG_HAVE_DEVICE:
                    // Popup AlertDialog, 同时进行安全性检测&注册设备
                    addNewDeviceDialog();
                    break;
                case MSG_NO_DEVICE:
                    // 进入完善注册流程
                    // TODO
                    break;
                default:
                    break;
            }
        }
    };

    private void addNewDeviceDialog() {
        AlertDialog.Builder builder = new Builder(mActivity);
        builder.setTitle("新增设备");
        builder.setMessage("是否添加此设备为您名下的使用设备？\n添加将进行常规安全性检查\n" +
                "取消则退出程序");
        builder.setPositiveButton("添加", new OnClickListener() {
            Intent intent;

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Thread t = new Thread(new Runnable() {
                    
                    @Override
                    public void run() {
                        String checkResult = PolicyUtils.isDeviceComplianced(mActivity);
                        if(checkResult.equals(String.valueOf(CommonUtils.FAIL))) {
                           Toast.makeText(mActivity, "未同步到最新的安全策略，请检查网络连接", Toast.LENGTH_SHORT).show();
                        } else if (checkResult.equals(String.valueOf(CommonUtils.SUCCESS))){
                           boolean regResult = DeviceUtils.getInstance(mActivity).registerDevice(mActivity);
                           if (regResult == CommonUtils.SUCCESS) {
                               //注册成功，进入登录界面
                               mActivity.finish();
                           } else {
                               //注册失败，怎么做？？？ TODO or do nothing
                           }
                        } else {
                            Toast.makeText(mActivity, "未通过安全策略:"+checkResult+"\n"+"请检查设备并重试。", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                t.start();
            }
        });
        //不注册的话，该设备不能登录，直接退出
        builder.setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                CommonUtils.exitBYOD(mActivity);
            }
        });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userregister1);
        mActivity = this;
        initView();
    }

    private void initView() {
        userAccountET = (EditText) findViewById(R.id.userAccount);
        nextBt = (Button) findViewById(R.id.next);
        
        nextBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                userAccount = userAccountET.getText().toString();
                if (userAccount.length() == 0) {
                    Toast.makeText(mActivity, "用户名为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        int deviceNum = PolicyUtils.getUserDeviceNum(userAccount); // TODO fake data
                        if (deviceNum == 0) {
                            handler.sendEmptyMessage(MSG_NO_DEVICE);
                        } else {
                            handler.sendEmptyMessage(MSG_HAVE_DEVICE);
                        }
                    }
                });
                t.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }
}
