package com.byod.sms.data;

import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

import com.byod.data.IAsyncQuery;
import com.byod.data.MyAsyncQueryHandler;

public class SystemSMSAsyncQuery implements IAsyncQuery{
    private final AsyncQueryHandler mAsyncQueryHandler;
    private final Context mContext;
    public SystemSMSAsyncQuery(Context context, AsyncQueryHandler asyncQueryHandler) {
        this.mContext = context.getApplicationContext();
        this.mAsyncQueryHandler = asyncQueryHandler;
    }
    @Override
    public void startQuery() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人的Uri
        String[] projection = {
                BaseColumns._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DATA1,
                "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
        }; // 查询的列
        mAsyncQueryHandler.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc"); // 按照sort_key升序查询
    }

    @Override
    public void startDelete() {

    }

    @Override
    public void startDelete(int id) {

    }

    @Override
    public void startInsert(ContentValues values) {

    }

    public void delete(long contactsID) {
        Uri deleteUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactsID);
        Uri lookupUri = ContactsContract.Contacts.getLookupUri(mContext.getContentResolver(), deleteUri);
        int id = -1;
        if (lookupUri != Uri.EMPTY) {
            id = mContext.getContentResolver().delete(deleteUri, null, null);
        }
        ((MyAsyncQueryHandler)mAsyncQueryHandler).onDeleteComplete(0, null, id);
    }
}
