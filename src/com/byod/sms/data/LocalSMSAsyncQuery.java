package com.byod.sms.data;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import com.byod.data.IAsyncQuery;
import com.byod.data.db.ContactsContentProvider;

import static com.byod.data.db.DatabaseHelper.ContactsColumns.DISPLAY_NAME;
import static com.byod.data.db.DatabaseHelper.ContactsColumns.LOOKUP_KEY;
import static com.byod.data.db.DatabaseHelper.ContactsColumns.PHONE;
import static com.byod.data.db.DatabaseHelper.ContactsColumns.SORT_KEY;

public class LocalSMSAsyncQuery implements IAsyncQuery{
    private final AsyncQueryHandler mAsyncQueryHandler;
    public LocalSMSAsyncQuery(AsyncQueryHandler asyncQueryHandler) {
        this.mAsyncQueryHandler = asyncQueryHandler;
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
    public void startInsert(ContentValues values) {
        Uri uri = ContactsContentProvider.CONTACTS_URI;
        mAsyncQueryHandler.startInsert(0, null, uri, values);
    }
}
