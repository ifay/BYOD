/**
 * 
 */
package com.byod.info;

import com.byod.R;
import com.byod.application.UserInfoCompleteActivity;
import com.byod.utils.CommonUtils;

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

/**
 * 信息上报应用
 * @author ifay
 */
public class InfoActivity extends Activity{

    private static final String TAG = "InfoActivity";
    private String url = CommonUtils.ONLINE_SERVER + "/UIA/user/edituser.jsp?UserID="; //TODO 如何绕过admin的鉴别？？
    private String userID;

    private WebView registerView = null;
    private WebSettings webSettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.userregister2);
        Intent i = getIntent();
        userID = i.getStringExtra("UserID");
        url = url + userID;
        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        registerView = (WebView) findViewById(R.id.userRegisterView);
        //set view properties
        webSettings = registerView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(false);
        //set show view in this app
        registerView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                setTitle("Loading..." + progress + "%");
                setProgress(progress * 100);
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
                Toast.makeText(InfoActivity.this, "Oh no:" + description, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        Log.d(TAG, url);
        registerView.loadUrl(url);

    }
}
