package com.byod.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byod.BYODApplication;
import com.byod.R;
import com.byod.launcher.HomeScreen;
import com.byod.utils.CommonUtils;
import com.byod.utils.DeviceUtils;
import com.byod.utils.KeyboardUtil;
import com.byod.utils.PolicyUtils;
import com.byod.utils.RightUtil;

/**
 * @author ifay 
 * 注册设备
 * step1：输入用户名密码，查询用户名下是否已经有设备
 * step2：1否--进入用户注册页面
 *       1是--同步安全策略step3
 * step3：安全策略检查成功：发起注册设备请求
 * step4：设备请求被批准，可登录
 */

public class DeviceRegisterActivity extends Activity {

    private static final String TAG = "DeviceRegisterActivity";

    private static final int MSG_START_REGISTER = 1001;
    //增添设备，需要由其他已注册的设备确认。其他设备每次认证通过进入HomeScreen的时候去服务器查询下是否有需要处理的设备
    private static final int MSG_SYNC_POLICY_FAIL = 2002;
    private static final int MSG_POLICY_PASS = 3000;
    private static final int MSG_POLICY_FAIL = 3001;
    private static final int MSG_REGISTER_REQUEST_SENT = 4000;
    private static final int MSG_REGISTER_REQUEST_SENT_FAILED = 4001;
    private static final int MSG_REGISTER_APPROVED = 5000;
    private static final int MSG_REGISTER_DECLINED = 5001;
    private static final int MSG_AUTH_FAIL = 6000;
    private static final int MSG_AUTH_USER_LOGOFF = 7000;   //用户不存在|已注销
    
    private static String POLICY_FAIL_REASON = "policy_fail_reason";

    private int authFailCount = 0;
    
    private DeviceRegisterActivity mActivity;
    private EditText userAccountET = null;
    private EditText pwdET = null;
    private Button nextBt = null;
    private Button getKeyboardBtn = null;
    private static KeyboardUtil keyboard = null;
    private String userAccount = "";
    private String userPwd = "";
    private boolean mIsFirstDevice = false;

    private String keyboardJson;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            Intent i;
            switch (what) {
                case MSG_START_REGISTER:
                    //sync policy
                    nextBt.setText("同步最新安全策略并检测");
                    nextBt.setOnClickListener(syncAndCheckPolicy);
                    break;
                case MSG_SYNC_POLICY_FAIL:
                    nextBt.setText("策略同步失败 请重试");
                    nextBt.setOnClickListener(syncAndCheckPolicy);
                    break;
                case MSG_POLICY_FAIL:
                    Bundle data = msg.getData();
                    String reason = data.getString(POLICY_FAIL_REASON);
                    nextBt.setText("安全策略检测"+reason+"未通过\n注册失败");
                    nextBt.setOnClickListener(exit);
                    break;
                case MSG_POLICY_PASS:
                    //send register device request
                    nextBt.setText("策略检测通过\n发送注册请求");
                    nextBt.setOnClickListener(sendRegReqToPeer);
                    break;
                case MSG_REGISTER_APPROVED:
                    nextBt.setText("完成注册");
                    nextBt.setOnClickListener(finishRegisteration);
                    break;
                case MSG_REGISTER_DECLINED:
                    nextBt.setText("注册未通过，点击退出");
                    nextBt.setOnClickListener(exit);
                    break;
                case MSG_REGISTER_REQUEST_SENT:
                    nextBt.setText("刷新请求结果");
                    nextBt.setOnClickListener(refresh);
                    break;
                case MSG_REGISTER_REQUEST_SENT_FAILED:
                    nextBt.setText("发送失败，重新发送");
                    nextBt.setOnClickListener(sendRegReqToPeer);
                    break;
                case MSG_AUTH_FAIL:
                    authFailCount++;
                    Toast.makeText(mActivity, "密码错误", Toast.LENGTH_SHORT).show();
                    if (authFailCount >= 3) {
                        //server will Lock user TODO Server
                        Toast.makeText(mActivity, "登录错误次数超过3次，应用退出", Toast.LENGTH_LONG).show();
                        BYODApplication.getInstance().exit();
                    }
                    break;
                case MSG_AUTH_USER_LOGOFF:
                    //用户已注销，无法再次登录，设备上的相关信息擦除
                    Toast.makeText(mActivity, "该用户已注销，BYOD将安全退出", Toast.LENGTH_LONG).show();
                    BYODApplication.getInstance().exit();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        BYODApplication.getInstance().addActivity(this);
        setContentView(R.layout.device_register);
        mActivity = this;
        initView();
    }

    //【下一步】按钮功能：获取用户名下设备数目(验证用户名、密码)
    View.OnClickListener checkUserDeviceListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userAccount = userAccountET.getText().toString().trim();
            userPwd = pwdET.getText().toString().trim();
            if (userPwd.length() == 0) {
                Toast.makeText(mActivity, "密码为空！", Toast.LENGTH_SHORT).show();
                return;
            }

            // 服务器验证用户名及密码，返回-1：密码不匹配，0无设备，n个设备
            int deviceNum;
            try {
                deviceNum = DeviceUtils.getUserDeviceNum(userAccount, userPwd);
                if (deviceNum == -1) {
                    Log.d(TAG,"deviceNum is "+deviceNum);
                    handler.sendEmptyMessage(MSG_AUTH_FAIL);    ///////TODO MSG_AUTH_FAIL
                } else if (deviceNum == -2) {
                    handler.sendEmptyMessage(MSG_AUTH_USER_LOGOFF); //用户已注销，清除数据，退出系统
                } else {
                    mIsFirstDevice = (deviceNum == 0) ;
                    handler.sendEmptyMessage(MSG_START_REGISTER);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //【获取键盘】
    private OnClickListener getKeyboard = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            String userAccount = userAccountET.getText().toString();
            if (userAccount == null || userAccount.length() < 1) {
                Toast.makeText(mActivity, "请输入用户名", Toast.LENGTH_SHORT).show();
                return ;
            }
            
            //1. new keyboard
            if (keyboard == null) {
                keyboard = new KeyboardUtil(mActivity, mActivity, pwdET, R.id.keyboard_view);
            }
            
            //2. get keyboard from server
            try {
                keyboardJson = keyboard.getRandomKeyboard(userAccount.trim());
                if (keyboardJson.length() < 1) {
                    Toast.makeText(mActivity, "键盘获取失败，请重试", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    v.setVisibility(View.GONE);
                    //3. show password edittext
                    pwdET.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                Toast.makeText(mActivity, "键盘获取失败，请重试", Toast.LENGTH_SHORT).show();
                return ;
            }

        }
    };

    
    //【完善用户信息】
    View.OnClickListener userInfoComplete = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(mActivity,UserInfoCompleteActivity.class);
            startActivity(i);
        }
    };


    //【同步最新安全策略】
    View.OnClickListener syncAndCheckPolicy = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String rst;
            try {
                PolicyUtils.getDevicePolicyByUser(mActivity, userAccount);
                rst = PolicyUtils.checkPolicy(mActivity);
                if (rst == null) {
                    handler.sendEmptyMessage(MSG_POLICY_PASS);
                } else {
                    handler.sendEmptyMessage(MSG_POLICY_FAIL);
                    
                    Message msg = new Message();
                    msg.what = MSG_POLICY_FAIL;
                    Bundle data = new Bundle();
                    data.putString(POLICY_FAIL_REASON, rst);
                    msg.setData(data);
                    handler.sendMessage(msg);
                    
                }
            } catch (Exception e) {
//                handler.sendEmptyMessage(MSG_POLICY_PASS);
                 handler.sendEmptyMessage(MSG_SYNC_POLICY_FAIL);
                e.printStackTrace();
            }
        }
    };
    
//    //【检测最新安全策略】
//    View.OnClickListener checkPolicy = new View.OnClickListener() {
//        
//        @Override
//        public void onClick(View v) {
//            int rst = DeviceUtils.isDeviceComplianced(mActivity);
//            if (rst == 0) {
//                handler.sendEmptyMessage(MSG_POLICY_PASS);
//            } else {
//                handler.sendEmptyMessage(MSG_POLICY_FAIL);
//            }
//        }
//    };


    //【发起注册设备请求】 请求发往服务器
    View.OnClickListener sendRegReqToPeer = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            boolean rst;
            String userAccount = userAccountET.getText().toString().trim();
            try {
                rst = DeviceUtils.getInstance(mActivity).registerDevice(mActivity, mIsFirstDevice, userAccount);
                if (rst ==CommonUtils.SUCCESS) {
                    if ( mIsFirstDevice ) {
                        handler.sendEmptyMessage(MSG_REGISTER_APPROVED);
                        return;
                    } else {
                        handler.sendEmptyMessage(MSG_REGISTER_REQUEST_SENT);
                    }
                } else {
                    handler.sendEmptyMessage(MSG_REGISTER_REQUEST_SENT_FAILED);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    
    //【刷新请求结果】向服务器查询注册结果 （isDeviceActive字段）
    View.OnClickListener refresh = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            int approved;
            try {
                approved = DeviceUtils.getInstance(mActivity).checkRegRequestApproved();
                if (approved == 1) {
                    handler.sendEmptyMessage(MSG_REGISTER_APPROVED);
                } else if (approved == 0) {
                    handler.sendEmptyMessage(MSG_REGISTER_REQUEST_SENT);    //点击刷新
                } else {
                    handler.sendEmptyMessage(MSG_REGISTER_DECLINED);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //【完成注册】 保存userAccount，并跳转进入Homescreen
    View.OnClickListener finishRegisteration = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            boolean rst;
            CommonUtils.setPrefString(mActivity, CommonUtils.PREF_KEY_USERACCOUNT, userAccount);
            Intent i = new Intent(mActivity, HomeScreen.class);
            i.putExtra("isLoggedIn", true);
            startActivity(i);
        }
    };

    //【注册失败，点击退出】清除所有local数据，退出应用z
    View.OnClickListener exit = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            //1.clear all Policy local
            CommonUtils.deleteLocalPolicy(mActivity);
            BYODApplication.getInstance().exit();
        }
    };

    //use custom keyboard
    View.OnTouchListener showKeyboard = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int inputType = ((EditText)v).getInputType();
            ((EditText)v).setInputType(InputType.TYPE_NULL);
            
            String userAccount = userAccountET.getText().toString();
            try {
                keyboard.showRandomKeyboard(keyboardJson);
                keyboard.showKeyboard();
            } catch (Exception e) {
                Toast.makeText(mActivity, "键盘加载失败，请重试", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            
            ((EditText)v).setInputType(inputType);
            return false;
        }
    };


    private void initView() {
        userAccountET = (EditText) findViewById(R.id.userAccount);
        nextBt = (Button) findViewById(R.id.next);
        getKeyboardBtn = (Button) findViewById(R.id.getKeyboard);
        pwdET = (EditText) findViewById(R.id.pwd);
        pwdET.setOnTouchListener(showKeyboard);
        nextBt.setOnClickListener(checkUserDeviceListener);
        getKeyboardBtn.setOnClickListener(getKeyboard);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BYODApplication.getInstance().removeActivity(this);
    }
    
    @Override
    public void onBackPressed() {
        // clear Keyboard first
        if (keyboard != null && keyboard.keyboardIsShown()) {
            keyboard.hideKeyboard();
            keyboard = null;
        }
        super.onBackPressed();
    }
}
