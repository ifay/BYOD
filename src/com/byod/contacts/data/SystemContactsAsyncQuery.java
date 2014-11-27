package com.byod.contacts.data;

import android.content.AsyncQueryHandler;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import com.byod.data.IAsyncQuery;

public class SystemContactsAsyncQuery implements IAsyncQuery{
    private final AsyncQueryHandler mAsyncQueryHandler;
    public SystemContactsAsyncQuery(AsyncQueryHandler asyncQueryHandler) {
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
}
