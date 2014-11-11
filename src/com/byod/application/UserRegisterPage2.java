package com.byod.application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.byod.R;
import com.byod.utils.CommonUtils;

public class UserRegisterPage2 extends Activity {

    private static final String TAG = "UserRegisterPage2";
    private String url =CommonUtils.ONLINE_SERVER+"/UIA/user/edituser.jsp?UserID="; //TODO 如何绕过admin的鉴别？？
    private String userID;
    //http://localhost:8080/UIA/user/edituser.jsp?UserID=2328

    private WebView registerView = null;
    private WebSettings webSettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.userregister2);
        Intent i = getIntent();
        userID = i.getStringExtra("UserID");
        url = url + userID;
        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        registerView = (WebView)findViewById(R.id.userRegisterView);
        //set view properties
        webSettings = registerView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(false);
        //set show view in this app
        registerView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                setTitle("Loading..."+progress+"%");
                setProgress(progress*100);
                if (progress == 100) {
                    setTitle(R.string.app_name);
                }
            }
        });
        registerView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                    String failingUrl) {
                Toast.makeText(UserRegisterPage2.this, "Oh no:" + description, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        Log.d(TAG, url);
        registerView.loadUrl(url);
        
    }
    
    
//    private UserRegisterPage2 mActivity;
//    private Intent intent;
//    private String userAccount;
//    
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.userregister2);
//        mActivity = this;
//        intent = getIntent();
//        userAccount = intent.getStringExtra("UserName");
//        initView();
//    }
//
//    private void initView() {
//        Thread t = new Thread(new Runnable() {
//            
//            @Override
//            public void run() {
//                //从服务器读取已存的用户信息，展示在layout上
//            }
//        });
//        t.start();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }

}
