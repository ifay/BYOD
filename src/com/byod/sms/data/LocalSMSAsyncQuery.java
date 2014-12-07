package com.byod.sms.data;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.net.Uri;

import com.byod.data.IAsyncQuery;
import com.byod.data.db.ContactsContentProvider;

import static com.byod.data.db.DatabaseHelper.SMSColumns;

public class LocalSMSAsyncQuery implements IAsyncQuery{
    private final AsyncQueryHandler mAsyncQueryHandler;
    public LocalSMSAsyncQuery(AsyncQueryHandler asyncQueryHandler) {
        this.mAsyncQueryHandler = asyncQueryHandler;
    }
    @Override
    public void startQuery() {
        Uri uri = ContactsContentProvider.SMS_URI; // 联系人的Uri
        String[] projection = {
                SMSColumns._ID,//0
                SMSColumns.TYPE,//1
                SMSColumns.ADDRESS,//2
                SMSColumns.BODY,//3
                SMSColumns.DATE,//4
                SMSColumns.THREAD_ID,//5
                SMSColumns.READ,//6
                SMSColumns.PROTOCOL//7
        }; // 查询的列
        mAsyncQueryHandler.startQuery(0, null, uri, projection, null, null,
                "date COLLATE LOCALIZED asc"); // 按照sort_key升序查询
    }

    @Override
    public void startInsert(ContentValues values) {
        Uri uri = ContactsContentProvider.SMS_URI;
        mAsyncQueryHandler.startInsert(0, null, uri, values);
    }
}
