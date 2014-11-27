package com.byod.contacts.data.db;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * 联系人的Database helper. 设计成单例以确保所有的 {@link android.content.ContentProvider}
 * 使用的是同一个引用.
 */
public class ContactsDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "ContactsDatabaseHelper";
    /**
     * Contacts DB 版本号
     * <pre>
     *   0  第一个版本
     * </pre>
     */
    static final int DATABASE_VERSION = 0;

    private static final String DATABASE_NAME = "contacts.db";
    public static final String TABLE_NAME = "contacts";

    private static ContactsDatabaseHelper sSingleton = null;

    public static synchronized ContactsDatabaseHelper getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new ContactsDatabaseHelper(context, DATABASE_NAME);
        }
        return sSingleton;
    }

    protected ContactsDatabaseHelper(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
        Resources resources = context.getResources();
    }

    public SQLiteDatabase getDatabase(boolean writable) {
        return writable ? getWritableDatabase() : getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Bootstrapping database version: " + DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
