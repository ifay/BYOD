package com.byod.contacts.service;
//T9搜索

import android.app.Service;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

import com.byod.BYODApplication;
import com.byod.contacts.bean.ContactBean;

import java.util.ArrayList;

public class T9Service extends Service {

    private AsyncQueryHandler asyncQuery;
    private static final String LOGTAG = "T9Service";

    @Override
    public void onCreate() {
    }

    @Override
    public void onStart(Intent intent, int startId) {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        asyncQuery = new MyAsyncQueryHandler(getContentResolver());
        initSQL();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onRebind(Intent intent) {
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    protected void initSQL() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {
                BaseColumns._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DATA1,
                "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        };
        asyncQuery.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc");
    }

    private class MyAsyncQueryHandler extends AsyncQueryHandler {
        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            querying(cursor);
        }
    }

    private void querying(final Cursor cursor) {

        Handler handlerInsertOrder = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MAsyncTask.DOWNLOADING_START_MESSAGE:

                        break;


                    case MAsyncTask.DOWNLOAD_END_MESSAGE:
                        Bundle bundle1 = msg.getData();
                        ArrayList<ContactBean> list = (ArrayList<ContactBean>) bundle1.get("完成");
                        BYODApplication ma = (BYODApplication) getApplication();
                        System.out.println(list.size());
                        ma.setContactBeanList(list);
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };

        MAsyncTask.startRequestServerData(T9Service.this, handlerInsertOrder, cursor);
    }
}
