package com.byod.contacts.data;

import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.BaseColumns;

import com.byod.data.IAsyncQuery;
import com.byod.data.db.ContactsContentProvider;
import com.byod.sms.service.SMSObserverService;

import static com.byod.data.db.DatabaseHelper.ContactsColumns.DISPLAY_NAME;
import static com.byod.data.db.DatabaseHelper.ContactsColumns.LOOKUP_KEY;
import static com.byod.data.db.DatabaseHelper.ContactsColumns.PHONE;
import static com.byod.data.db.DatabaseHelper.ContactsColumns.SORT_KEY;

public class LocalContactsAsyncQuery implements IAsyncQuery{
    private final AsyncQueryHandler mAsyncQueryHandler;
    private final Context mContext;
    public LocalContactsAsyncQuery(Context context, AsyncQueryHandler asyncQueryHandler) {
        this.mAsyncQueryHandler = asyncQueryHandler;
        this.mContext = context.getApplicationContext();
    }
    @Override
    public void startQuery() {
        Uri uri = ContactsContentProvider.CONTACTS_URI; // 联系人的Uri
        String[] projection = {
                BaseColumns._ID,
                DISPLAY_NAME,
                PHONE,
                SORT_KEY,
                LOOKUP_KEY
        }; // 查询的列
        mAsyncQueryHandler.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc"); // 按照sort_key升序查询
    }

    @Override
    public void startDelete() {
        Uri uri = ContactsContentProvider.CONTACTS_URI;
        mAsyncQueryHandler.startDelete(0, null, uri, null, null);
    }

    @Override
    public void startDelete(int id) {
        Uri uri = ContentUris.withAppendedId(ContactsContentProvider.CONTACTS_URI, id);
        mAsyncQueryHandler.startDelete(0, null, uri, null, null);
    }

    @Override
    public void startInsert(ContentValues values) {
        Uri uri = ContactsContentProvider.CONTACTS_URI;
        mAsyncQueryHandler.startInsert(0, null, uri, values);
        Intent intent = new Intent(SMSObserverService.ContactsChange.ACTION_CONTACTS_CHANGE);
        mContext.sendBroadcast(intent);
    }
}
