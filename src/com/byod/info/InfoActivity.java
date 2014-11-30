/**
 * 
 */
package com.byod.info;

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

import com.byod.AuthenticateActivity;
import com.byod.R;
import com.byod.utils.CommonUtils;

/**
 * 信息上报应用
 * @author ifay
 */
public class InfoActivity extends Activity{

    private static final String TAG = "InfoActivity";
    private InfoActivity mActivity = null;
    private String url = CommonUtils.ONLINE_SERVER + "/INFO/logon/LogonAction.action"; 
//    private String url2 = CommonUtils.ONLINE_SERVER + "/INFO";
    private String userID;

    private WebView infoView = null;
    private WebSettings webSettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        Log.d(TAG, "onCreate");
        setContentView(R.layout.app_info);
        String userAccount = CommonUtils.getPrefString(mActivity, CommonUtils.PREF_KEY_USERACCOUNT, "");
        Log.d(TAG, "userAccount"+userAccount);
        if (userAccount == null || userAccount.length() < 1) {
            //未登录
            Toast.makeText(mActivity, "请先登录", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(mActivity, AuthenticateActivity.class);
            startActivity(i);
        }
        url = url+"?userid="+userAccount+"&password=yyf123456!"; //TODO
        Log.d(TAG, url);
        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        infoView = (WebView) findViewById(R.id.info);
        //set view properties
        webSettings = infoView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(false);
        //set show view in this app
        infoView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                setTitle("Loading..." + progress + "%");
                setProgress(progress * 100);
                if (progress == 100) {
                    setTitle(R.string.app_name);
                }
            }
        });
        infoView.setWebViewClient(new WebViewClient() {

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
        infoView.loadUrl(url);
    }
}
