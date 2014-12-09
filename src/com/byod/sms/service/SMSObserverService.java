package com.byod.sms.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.byod.data.IAsyncQuery;
import com.byod.data.IAsyncQueryFactory;
import com.byod.data.IAsyncQueryHandler;
import com.byod.data.db.ContactsContentProvider;
import com.byod.sms.data.SMSAsyncQueryFactory;

import static com.byod.data.db.DatabaseHelper.ContactsColumns;
import static com.byod.data.db.DatabaseHelper.SMSColumns;

public class SMSObserverService extends Service implements IAsyncQueryHandler {
    private static final String TAG = "SMSObserverService";

    private class SMSHandler extends Handler {
        public void handleMessage(Message message)
        {
            Log.i(TAG,  "handleMessage: " + message);

            MessageItem item = (MessageItem) message.obj;
            if (isHaveNumber(item.getPhone())) {
                //delete the sms
                Uri uri = ContentUris.withAppendedId(ContactsContentProvider.SMS_CONTENT_URI, item.getId());
                getContentResolver().delete(uri, null, null);
                Log.i(TAG,  "delete sms item: " + item);

                ContentValues values = new ContentValues();
                values.put(SMSColumns.BODY, item.getBody());
                values.put(SMSColumns.ADDRESS, item.getPhone());
                values.put(SMSColumns.PROTOCOL, item.getProtocol());
                values.put(SMSColumns.TYPE, item.getType());
                values.put(SMSColumns.DATE, item.getDate());
                values.put(SMSColumns.READ, item.getRead());
                values.put(SMSColumns.THREAD_ID, item.getThreadId());

                IAsyncQuery query = mAsyncQueryFactory.getLocalAsyncQuery();
                query.startInsert(values);
            }
        }
    }

    private static class SMSObserver extends ContentObserver {
        public static final String TAG = "SMSObserver";

        private static final String[] PROJECTION = new String[]
        {
            SMSColumns._ID,//0
            SMSColumns.TYPE,//1
            SMSColumns.ADDRESS,//2
            SMSColumns.BODY,//3
            SMSColumns.DATE,//4
            SMSColumns.THREAD_ID,//5
            SMSColumns.READ,//6
            SMSColumns.PROTOCOL//7
        };

//        private static final String SELECTION =
//                SMSColumns._ID  + " > %s" +
//                        " and (" + SMSColumns.TYPE + " = " + SMSColumns.MESSAGE_TYPE_INBOX +
//                        " or " + SMSColumns.TYPE + " = " + SMSColumns.MESSAGE_TYPE_SENT + ")";

        private static final int COLUMN_INDEX_ID    = 0;
        private static final int COLUMN_INDEX_TYPE  = 1;
        private static final int COLUMN_INDEX_PHONE = 2;
        private static final int COLUMN_INDEX_BODY  = 3;
        private static final int COLUMN_INDEX_DATE  = 4;
        private static final int COLUMN_INDEX_THREAD_ID  = 5;
        private static final int COLUMN_INDEX_READ  = 6;
        private static final int COLUMN_INDEX_PROTOCOL = 7;

        private static final int MAX_NUMS = 10;
        private static int MAX_ID = 0;

        private ContentResolver mResolver;
        private Handler mHandler;

        public SMSObserver(ContentResolver contentResolver, Handler handler)
        {
            super(handler);
            this.mHandler = handler;
            this.mResolver = contentResolver;
        }

        @Override
        public void onChange(boolean selfChange)
        {
            Log.i(TAG, "onChange : " + selfChange + "; " + MAX_ID + "; ");
            super.onChange(selfChange);

            Cursor cursor = mResolver.query(ContactsContentProvider.SMS_CONTENT_URI, PROJECTION,
                    null, null, null);

            int id, type, protocol, thread_id, read;
            long date;
            String phone, body;
            Message message;
            MessageItem item;

            int iter = 0;
            boolean hasDone = false;

            while (cursor.moveToNext())
            {
                id = cursor.getInt(COLUMN_INDEX_ID);
                thread_id = cursor.getInt(COLUMN_INDEX_THREAD_ID);
                date = cursor.getLong(COLUMN_INDEX_DATE);
                read = cursor.getInt(COLUMN_INDEX_READ);
                type = cursor.getInt(COLUMN_INDEX_TYPE);
                phone = cursor.getString(COLUMN_INDEX_PHONE);
                body = cursor.getString(COLUMN_INDEX_BODY);
                protocol = cursor.getInt(COLUMN_INDEX_PROTOCOL);
                Log.d(TAG, "smsID: " + id + " phone: " + phone + " body: " + body + " prot: " + protocol);
                if (hasDone)
                {
                    MAX_ID = id;
                    break;
                }

                if (body != null)
                {
                    hasDone = true;
                    item = new MessageItem();
                    item.setId(id);
                    item.setType(type);
                    item.setPhone(phone);
                    item.setBody(body);
                    item.setProtocol(protocol);
                    item.setThreadId(thread_id);
                    item.setDate(date);
                    item.setRead(read);

                    message = new Message();
                    message.obj = item;
                    mHandler.sendMessage(message);
                } else {
                    if (id > MAX_ID) MAX_ID = id;
                }
                if (iter > MAX_NUMS) break;
                iter ++;
            }
        }
    }

    private ContentObserver mObserver;
    private Handler mHandler = new SMSHandler();
    private IAsyncQueryFactory mAsyncQueryFactory;

    @Override
    public void onCreate() {
        addSMSObserver();
        mAsyncQueryFactory = new SMSAsyncQueryFactory(this, this);
    }

    public void addSMSObserver()
    {
        Log.i(TAG, "add a SMS observer. ");
        ContentResolver resolver = getContentResolver();
        mObserver = new SMSObserver(resolver, mHandler);
        resolver.registerContentObserver(ContactsContentProvider.SMS_CONTENT_URI, true, mObserver);
    }

    @Override

    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onDestroy()
    {
        Log.i(TAG, "onDestroy().");
        this.getContentResolver().unregisterContentObserver(mObserver);
        super.onDestroy();
    }

    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {

    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {

    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {

    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {

    }

    public boolean isHaveNumber(String number) {
        String[] projection = {ContactsColumns.DISPLAY_NAME,};
        Cursor cursor = this.getContentResolver().query(
                ContactsContentProvider.CONTACTS_URI,
                projection,
                ContactsColumns.PHONE + " = '" + number + "'",
                null,
                null);
        if (cursor == null || cursor.getCount() <= 0) {
            return false;
        }

        return true;
    }
}
