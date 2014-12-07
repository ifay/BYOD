package com.byod.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Intents.Insert;
import android.provider.Telephony;
import android.util.Log;


/**
 * 联系人的Database helper. 设计成单例以确保所有的 {@link android.content.ContentProvider}
 * 使用的是同一个引用.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "ContactsDatabaseHelper";
    /**
     * Contacts DB 版本号
     * <pre>
     *   0  第一个版本
     * </pre>
     */
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contacts.db";

    private static DatabaseHelper sSingleton = null;


    public interface Tables {
        static final String CONTACTS_TABLE = "contacts";
        static final String CALLS_TABLE = "calls";
        static final String SMS_TABLE = "sms";
    }

    public interface ContactsColumns {
        public static final String _ID = BaseColumns._ID;
        public static final String DISPLAY_NAME = Contacts.DISPLAY_NAME;
        public static final String LOOKUP_KEY = Contacts.LOOKUP_KEY;
        public static final String SORT_KEY = Contacts.SORT_KEY_PRIMARY;
        public static final String PHOTO_ID = Contacts.PHOTO_ID;
        public static final String PHOTO_FILE_ID = Contacts.PHOTO_FILE_ID;
        public static final String PHOTO_URI = Contacts.PHOTO_URI;
        public static final String PHOTO_THUMBNAIL_URI = Contacts.PHOTO_THUMBNAIL_URI;
        public static final String STARRED = Contacts.STARRED;
        public static final String HAS_PHONE_NUMBER = Contacts.HAS_PHONE_NUMBER;
        public static final String LAST_UPDATED_TIMESTAMP = Contacts.CONTACT_LAST_UPDATED_TIMESTAMP;
        public static final String LAST_TIME_CONTACTED = Contacts.LAST_TIME_CONTACTED;
        public static final String TIMES_CONTACTED = Contacts.TIMES_CONTACTED;
        public static final String JOB_TITLE = Insert.JOB_TITLE;
        public static final String PHONE = Insert.PHONE;
        public static final String PHONE_TYPE = Insert.PHONE_TYPE;
        public static final String SECONDARY_PHONE = Insert.SECONDARY_PHONE;
        public static final String SECONDARY_PHONE_TYPE = Insert.SECONDARY_PHONE_TYPE;
        public static final String EMAIL = Insert.EMAIL;
        public static final String EMAIL_TYPE = Insert.EMAIL_TYPE;
        public static final String SECONDARY_EMAIL = Insert.SECONDARY_EMAIL;
        public static final String SECONDARY_EMAIL_TYPE = Insert.SECONDARY_EMAIL_TYPE;
    }

    public interface ContactsColumnsValue {
        public static final int STARRED_YES = 1;
        public static final int STATTED_NO = 0;
        public static final int HAS_PHONE_NUMBER_YES = 1;
        public static final int HAS_PHONE_NUMBER_NO = 0;
        public static final int PHONE_TYPE_WORK = 0;
        public static final int PHONE_TYPE_HOME = 1;
        public static final int PHONE_TYPE_OTHER = 2;
        public static final int EMAIL_TYPE_WORK = 0;
        public static final int EMAIL_TYPE_HOME = 1;
        public static final int EMAIL_TYPE_OTHER = 2;
    }

    public interface CallsColumns {
        public static final String _ID = BaseColumns._ID;
        public static final String NUMBER = Calls.NUMBER;
        public static final String DATE = Calls.DATE;
        public static final String TYPE = Calls.TYPE;
        public static final String CACHED_NAME = Calls.CACHED_NAME;
        public static final String DURATION = Calls.DURATION;
        public static final String COUNTRY_ISO = Calls.COUNTRY_ISO;
        public static final String GEOCODED_LOCATION = Calls.GEOCODED_LOCATION;
        public static final String CACHED_NUMBER_TYPE = Calls.CACHED_NUMBER_TYPE;
        public static final String CACHED_NUMBER_LABEL = Calls.CACHED_NUMBER_LABEL;
        public static final String CACHED_LOOKUP_URI = Calls.CACHED_LOOKUP_URI;
        public static final String CACHED_PHOTO_ID = Calls.CACHED_PHOTO_ID;
        public static final String CACHED_FORMATTED_NUMBER = Calls.CACHED_FORMATTED_NUMBER;
        public static final String IS_READ = Calls.IS_READ;
    }

    public interface CallsColumnsValue {
        /** Call log type for incoming calls. */
        public static final int TYPE_INCOMING = 1;
        /** Call log type for outgoing calls. */
        public static final int TYPE_OUTGOING = 2;
        /** Call log type for missed calls. */
        public static final int TYPE_MISSED = 3;
        public static final int IS_READ_YES = 1;
        public static final int IS_READ_NO = 0;
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new DatabaseHelper(context, DATABASE_NAME);
        }
        return sSingleton;
    }

    protected DatabaseHelper(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
        Resources resources = context.getResources();
    }

    public SQLiteDatabase getDatabase(boolean writable) {
        return writable ? getWritableDatabase() : getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Bootstrapping database version: " + DATABASE_VERSION);
        createContactsTable(db);
        createCallsTable(db);
        createSMSsTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void createContactsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Tables.CONTACTS_TABLE + " (" +
                ContactsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ContactsColumns.DISPLAY_NAME + " TEXT, " +
                ContactsColumns.LOOKUP_KEY + " TEXT, " +
                ContactsColumns.SORT_KEY + " TEXT, " +
                ContactsColumns.PHOTO_ID + " INTEGER, " +
                ContactsColumns.PHOTO_FILE_ID + " INTEGER, " +
                ContactsColumns.PHOTO_URI + " TEXT, " +
                ContactsColumns.PHOTO_THUMBNAIL_URI + " TEXT, " +
                ContactsColumns.STARRED + " INTEGER NOT NULL DEFAULT 0, " +
                ContactsColumns.HAS_PHONE_NUMBER + " INTEGER NOT NULL DEFAULT 0, " +
                ContactsColumns.LAST_UPDATED_TIMESTAMP + " INTEGER, " +
                ContactsColumns.TIMES_CONTACTED + " INTEGER NOT NULL DEFAULT 0, " +
                ContactsColumns.LAST_TIME_CONTACTED + " INTEGER, " +
                ContactsColumns.JOB_TITLE + " TEXT, " +
                ContactsColumns.PHONE  + " TEXT, " +
                ContactsColumns.PHONE_TYPE  + " INTEGER NOT NULL DEFAULT 0, " +
                ContactsColumns.SECONDARY_PHONE + " TEXT, " +
                ContactsColumns.SECONDARY_PHONE_TYPE + " INTEGER NOT NULL DEFAULT 0, " +
                ContactsColumns.EMAIL + " TEXT, " +
                ContactsColumns.EMAIL_TYPE + " INTEGER NOT NULL DEFAULT 0, " +
                ContactsColumns.SECONDARY_EMAIL + " TEXT, " +
                ContactsColumns.SECONDARY_EMAIL_TYPE + " INTEGER NOT NULL DEFAULT 0" +
        ");");
    }

    private void createCallsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Tables.CALLS_TABLE + " (" +
                CallsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CallsColumns.NUMBER + " TEXT," +
                CallsColumns.DATE + " INTEGER," +
                CallsColumns.DURATION + " INTEGER," +
                CallsColumns.TYPE + " INTEGER," +
                CallsColumns.CACHED_NAME + " TEXT," +
                CallsColumns.CACHED_NUMBER_TYPE + " INTEGER," +
                CallsColumns.CACHED_NUMBER_LABEL + " TEXT," +
                CallsColumns.COUNTRY_ISO + " TEXT," +
                CallsColumns.IS_READ + " INTEGER," +
                CallsColumns.GEOCODED_LOCATION + " TEXT," +
                CallsColumns.CACHED_LOOKUP_URI + " TEXT," +
                CallsColumns.CACHED_PHOTO_ID + " INTEGER NOT NULL DEFAULT 0," +
                CallsColumns.CACHED_FORMATTED_NUMBER + " TEXT" +
        ");");
    }

    private void createSMSsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Tables.SMS_TABLE + " (" +
                Telephony.Mms._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Telephony.Mms.DATE + " INTEGER," +
                Telephony.Mms.DATE_SENT + " INTEGER DEFAULT 0," +
                Telephony.Mms.MESSAGE_BOX + " INTEGER," +
                Telephony.Mms.READ + " INTEGER DEFAULT 0," +
                Telephony.Mms.MESSAGE_ID + " TEXT," +
                Telephony.Mms.SUBJECT + " TEXT," +
                Telephony.Mms.SUBJECT_CHARSET + " INTEGER," +
                Telephony.Mms.CONTENT_TYPE + " TEXT," +
                Telephony.Mms.CONTENT_LOCATION + " TEXT," +
                Telephony.Mms.EXPIRY + " INTEGER," +
                Telephony.Mms.MESSAGE_CLASS + " TEXT," +
                Telephony.Mms.MESSAGE_TYPE + " INTEGER," +
                Telephony.Mms.MMS_VERSION + " INTEGER," +
                Telephony.Mms.MESSAGE_SIZE + " INTEGER," +
                Telephony.Mms.PRIORITY + " INTEGER," +
                Telephony.Mms.READ_REPORT + " INTEGER," +
                Telephony.Mms.REPORT_ALLOWED + " INTEGER," +
                Telephony.Mms.RESPONSE_STATUS + " INTEGER," +
                Telephony.Mms.STATUS + " INTEGER," +
                Telephony.Mms.TRANSACTION_ID + " TEXT," +
                Telephony.Mms.RETRIEVE_STATUS + " INTEGER," +
                Telephony.Mms.RETRIEVE_TEXT + " TEXT," +
                Telephony.Mms.RETRIEVE_TEXT_CHARSET + " INTEGER," +
                Telephony.Mms.READ_STATUS + " INTEGER," +
                Telephony.Mms.CONTENT_CLASS + " INTEGER," +
                Telephony.Mms.RESPONSE_TEXT + " TEXT," +
                Telephony.Mms.DELIVERY_TIME + " INTEGER," +
                Telephony.Mms.DELIVERY_REPORT + " INTEGER," +
                Telephony.Mms.LOCKED + " INTEGER DEFAULT 0," +
                Telephony.Mms.SEEN + " INTEGER DEFAULT 0," +
                Telephony.Mms.CREATOR + " TEXT," +
                Telephony.Mms.TEXT_ONLY + " INTEGER DEFAULT 0" +
                ");");
    }

    public long contactsInsert(ContentValues values) {
        return getWritableDatabase().insert(Tables.CONTACTS_TABLE, null, values);
    }

    public long callsInsert(ContentValues values) {
        return getWritableDatabase().insert(Tables.CALLS_TABLE, null, values);
    }

    public long smsInsert(ContentValues values) {
        return getWritableDatabase().insert(Tables.SMS_TABLE, null, values);
    }

}
