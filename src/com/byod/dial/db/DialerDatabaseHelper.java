package com.byod.dial.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class DialerDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DialerDatabaseHelper";
    private static final boolean DEBUG = false;

    private static DialerDatabaseHelper sSingleton = null;

    private static final Object mLock = new Object();
    private static final AtomicBoolean sInUpdate = new AtomicBoolean(false);
    private final Context mContext;

    /**
     * SmartDial DB version ranges:
     * <pre>
     *   0-98   KitKat
     * </pre>
     */
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "dialer.db";

    /**
     * Access function to get the singleton instance of DialerDatabaseHelper.
     */
    public static synchronized DialerDatabaseHelper getInstance(Context context) {
        if (DEBUG) {
            Log.v(TAG, "Getting Instance");
        }
        if (sSingleton == null) {
            // Use application context instead of activity context because this is a singleton,
            // and we don't want to leak the activity if the activity is not running but the
            // dialer database helper is still doing work.
            sSingleton = new DialerDatabaseHelper(context.getApplicationContext(),
                    DATABASE_NAME);
        }
        return sSingleton;
    }

    /**
     * Returns a new instance for unit tests. The database will be created in memory.
     */
    protected DialerDatabaseHelper(Context context, String databaseName) {
        this(context, databaseName, DATABASE_VERSION);
    }

    protected DialerDatabaseHelper(Context context, String databaseName, int dbVersion) {
        super(context, databaseName, null, dbVersion);
        mContext = context;
    }

    /**
     * Creates tables in the database when database is created for the first time.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldNumber, int newNumber) {
    }
}
