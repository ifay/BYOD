package com.byod.sms.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.byod.data.IAsyncQuery;
import com.byod.data.IAsyncQueryFactory;
import com.byod.data.db.ContactsContentProvider;
import com.byod.sms.data.SMSAsyncQueryFactory;

import static com.byod.data.db.DatabaseHelper.ContactsColumns;
import static com.byod.data.db.DatabaseHelper.SMSColumns;

public class SMSObserverService extends Service {
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

                if (null == mAsyncQueryFactory) {
                    mAsyncQueryFactory = new SMSAsyncQueryFactory(SMSObserverService.this, null);
                    mAsyncQuery = mAsyncQueryFactory.getLocalAsyncQuery();
                }
                mAsyncQuery.startInsert(values);
            }
        }
    }

    private class SMSObserver extends ContentObserver {
        public static final String TAG = "SMSObserver";

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public SMSObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange)
        {
            Log.d(TAG, "onChange : " + selfChange);
            super.onChange(selfChange);
            doOnChange(false);

        }
    }

    public class ContactsChange extends BroadcastReceiver {
        public static final String ACTION_CONTACTS_CHANGE = "action_contacts_change";
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_CONTACTS_CHANGE.equals(intent.getAction())) {
                doOnChange(true);
            }
        }
    }

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

    private static final int COLUMN_INDEX_ID    = 0;
    private static final int COLUMN_INDEX_TYPE  = 1;
    private static final int COLUMN_INDEX_PHONE = 2;
    private static final int COLUMN_INDEX_BODY  = 3;
    private static final int COLUMN_INDEX_DATE  = 4;
    private static final int COLUMN_INDEX_THREAD_ID  = 5;
    private static final int COLUMN_INDEX_READ  = 6;
    private static final int COLUMN_INDEX_PROTOCOL = 7;

    private ContentObserver mObserver;
    private Handler mHandler = new SMSHandler();
    private IAsyncQueryFactory mAsyncQueryFactory;
    private IAsyncQuery mAsyncQuery;
    private BroadcastReceiver mContactsChange;

    @Override
    public void onCreate() {
        addSMSObserver();

        if (null == mContactsChange) {
            mContactsChange = new ContactsChange();
            registerReceiver(mContactsChange, new IntentFilter(ContactsChange.ACTION_CONTACTS_CHANGE));
        }
    }

    public void addSMSObserver()
    {
        Log.i(TAG, "add a SMS observer. ");
        ContentResolver resolver = getContentResolver();
        if (null == mObserver) {
            mObserver = new SMSObserver(mHandler);
            resolver.registerContentObserver(ContactsContentProvider.SMS_CONTENT_URI, true, mObserver);
        }
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
        if (null != mObserver) {
            this.getContentResolver().unregisterContentObserver(mObserver);
        }
        if (null != mContactsChange) {
            unregisterReceiver(mContactsChange);
        }
        super.onDestroy();
    }

    private void doOnChange(boolean all) {
        Cursor cursor = getContentResolver().query(ContactsContentProvider.SMS_CONTENT_URI, PROJECTION,
                null, null, null);

        int id, type, protocol, thread_id, read;
        long date;
        String phone, body;
        Message message;
        MessageItem item;

        boolean hasOne = false;

        while (cursor.moveToNext())
        {
            id = cursor.getInt(COLUMN_INDEX_ID);
            if (!all && hasOne)
            {
                break;
            }

            thread_id = cursor.getInt(COLUMN_INDEX_THREAD_ID);
            date = cursor.getLong(COLUMN_INDEX_DATE);
            read = cursor.getInt(COLUMN_INDEX_READ);
            type = cursor.getInt(COLUMN_INDEX_TYPE);
            phone = cursor.getString(COLUMN_INDEX_PHONE);
            body = cursor.getString(COLUMN_INDEX_BODY);
            protocol = cursor.getInt(COLUMN_INDEX_PROTOCOL);
            Log.d(TAG, "smsID: " + id + " phone: " + phone + " body: " + body + " protocol: " + protocol);

            if (body != null)
            {
                hasOne = true;
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
            }
        }
    }

    public boolean isHaveNumber(String number) {
        number = number.replaceAll("\\s", "");
        String[] projection = {ContactsColumns.DISPLAY_NAME};
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
