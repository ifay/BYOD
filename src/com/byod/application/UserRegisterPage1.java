
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
import android.widget.TextView;
import android.widget.Toast;

import com.byod.R;
import com.byod.utils.CommonUtils;
import com.byod.utils.DeviceUtils;
import com.byod.utils.PolicyUtils;

/**
 * @author ifay 完善注册页面，（调用条件：首次使用设备登录） 尝试web View方式实现
 *         步骤一（服务器端）：管理员在UIA上录入用户的基本信息，用户获得默认权限-最低的权限 
 *         步骤二：设备上没有策略信息：1-2-3（新用户） 或者 1-4（新设备）
 *              Page1: 输入用户名(可考虑做成翻页形式，“下一步”)
 *              Page2: 输入初始密码
 *              Page3：完善用户信息 （包括挑战问题）
 *              Page4：输入用户密码并添加设备  
 *         考虑把登陆INFO的方法做成服务？？还是直接用WebView展示？
 */

public class UserRegisterPage1 extends Activity {

    private static final String LOG_TAG = "UserRegisterPage1";

    private static final int MSG_NO_DEVICE = 1000; // 用户名下没有设备
    private static final int MSG_ADD_DEVICE = 1001;
    private static final int MSG_AUTH_USER_SUCCESS = 2000;  //用户名，密码匹配
    private static final int MSG_AUTH_USER_FAIL = 2001;  //用户名，密码不匹配
    private static final int MSG_FIRST_DEVICE = 3000;    //安全策略检查通过

    private UserRegisterPage1 mActivity;
    private EditText userAccountET = null;
    private EditText pwdET = null;
    private TextView textView2 = null;
    private Button nextBt = null;
    private String userAccount = "";
    private String userPwd = "";
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            Intent i;
            switch (what) {
                case MSG_ADD_DEVICE:
                    // 显示密码框
                    pwdET.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.VISIBLE);
                    nextBt.setText("增添此设备");
                    //TODO 验证用户名密码
                    break;
                case MSG_NO_DEVICE:
                    i = new Intent(mActivity, UserRegisterPage2.class);
                    startActivity(i);
                    // TODO
                    break;
                case MSG_FIRST_DEVICE:
                    // 显示密码框
                    pwdET.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.VISIBLE);
                    nextBt.setText("增添此设备并下一步");
                    //nextBt.setOnClickListener();
                case MSG_AUTH_USER_SUCCESS:
                    // Popup AlertDialog, 同时进行安全性检测&增添设备
                    addNewDeviceDialog();
                    break;
                case MSG_AUTH_USER_FAIL:
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
                        int checkResult = DeviceUtils.isDeviceComplianced(mActivity);   //TODO 如何和策略名對應上
                        if(checkResult > 0) {
                           Toast.makeText(mActivity, "未同步到最新的安全策略，请检查网络连接", Toast.LENGTH_SHORT).show();
                        } else if (checkResult == PolicyUtils.CODE_COMPLIANCED){
                            //通过安全策略，进行注册
                            Toast.makeText(mActivity, "通过安全策略，正在注册设备...", Toast.LENGTH_LONG).show();
                            boolean regResult = DeviceUtils.getInstance(mActivity).registerDevice(mActivity);
                           if (regResult == CommonUtils.SUCCESS) {
                               //注册成功，进入登录界面
                               setResult(Activity.RESULT_OK, mActivity.getIntent());
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
        pwdET = (EditText) findViewById(R.id.pwd);
        textView2 = (TextView) findViewById(R.id.textView2);
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
                        int deviceNum = DeviceUtils.getUserDeviceNum(userAccount); // TODO fake data
                        if (deviceNum == 0) {
                            PolicyUtils.getNewestPolicyByUser(userAccount);
                            int complianced = DeviceUtils.isDeviceComplianced(mActivity);
                            if (complianced == PolicyUtils.CODE_COMPLIANCED) {
                                handler.sendEmptyMessage(MSG_FIRST_DEVICE);
                            }
                        } else {
                            handler.sendEmptyMessage(MSG_ADD_DEVICE);
                        }
                    }
                });
                if (pwdET.getVisibility() != View.VISIBLE){
                    t.start();
                } else{
                    userPwd = pwdET.getText().toString();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
