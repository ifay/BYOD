package com.byod.data.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Log;

public class ContactsContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.byod";
    public static final int CONTACTS_ITEM = 1;
    public static final int CONTACTS_ITEM_ID = 2;
    public static final int CONTACTS_POS = 3;
    public static final int SMS_ITEM =4;
    public static final int SMS_ITEM_ID = 5;
    public static final int SMS_POS = 6;

    public static final Uri CONTACTS_URI = Uri.parse("content://" + AUTHORITY + "/contacts");
    public static final Uri SMS_URI = Uri.parse("content://" + AUTHORITY + "/sms");
    public static final Uri SMS_CONTENT_URI = Uri.parse("content://sms");

    private static final UriMatcher mUriMatcher;
    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, "contacts", CONTACTS_ITEM);
        mUriMatcher.addURI(AUTHORITY, "contacts/#", CONTACTS_ITEM_ID);
        mUriMatcher.addURI(AUTHORITY, "sms", SMS_ITEM);
        mUriMatcher.addURI(AUTHORITY, "sms/#", SMS_ITEM_ID);
    }

    private ContentResolver mResolver = null;
    private DatabaseHelper mDbHelper = null;

    public ContactsContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = 0;
        switch (mUriMatcher.match(uri)) {
            case CONTACTS_ITEM:
                count = db.delete(DatabaseHelper.Tables.CONTACTS_TABLE, selection, selectionArgs);
                break;
            case SMS_ITEM:
                count = db.delete(DatabaseHelper.Tables.SMS_TABLE, selection, selectionArgs);
                break;
            case CONTACTS_ITEM_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(DatabaseHelper.Tables.CONTACTS_TABLE,
                        DatabaseHelper.ContactsColumns._ID + "=" + id + (!TextUtils.isEmpty(selection) ?
                                " and (" + selection + ")" : ""), selectionArgs);
                break;
            case SMS_ITEM_ID:
                String sid = uri.getPathSegments().get(1);
                count = db.delete(DatabaseHelper.Tables.CONTACTS_TABLE,
                        DatabaseHelper.ContactsColumns._ID + "=" + sid + (!TextUtils.isEmpty(selection) ?
                                " and (" + selection + ")" : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Error Uri: " + uri);
        }
        mResolver.notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d("ContactsContentProvider", " insert: " + uri);
        Uri newUri;
        switch (mUriMatcher.match(uri)) {
            case CONTACTS_ITEM:
                long id = mDbHelper.contactsInsert(values);
                if (id < 0) {
                    throw new SQLiteException("Unable to insert " + values + "for" + uri);
                }
                newUri = ContentUris.withAppendedId(uri, id);
                mResolver.notifyChange(newUri, null);
                return newUri;
            case SMS_ITEM:
                long sid = mDbHelper.smsInsert(values);
                if (sid < 0) {
                    throw new SQLiteException("Unable to insert " + values + "for" + uri);
                }
                newUri = ContentUris.withAppendedId(uri, sid);
                mResolver.notifyChange(newUri, null);
                return newUri;
            default:
                throw new IllegalArgumentException("Error Uri: " + uri);
        }

    }

    @Override
    public boolean onCreate() {
        mResolver = getContext().getContentResolver();
        mDbHelper = DatabaseHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d("ContactsContentProvider", " query: " + uri);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        String limit = null;
        switch (mUriMatcher.match(uri)) {
            case CONTACTS_ITEM:
                sqlBuilder.setTables(DatabaseHelper.Tables.CONTACTS_TABLE);
               break;
            case CONTACTS_ITEM_ID:
                String id = uri.getPathSegments().get(1);
                sqlBuilder.setTables(DatabaseHelper.Tables.CONTACTS_TABLE);
                sqlBuilder.appendWhere(DatabaseHelper.ContactsColumns._ID + "=" + id);
                break;
            case SMS_ITEM:
                sqlBuilder.setTables(DatabaseHelper.Tables.SMS_TABLE);
                break;
            case SMS_ITEM_ID:
                String sId = uri.getPathSegments().get(1);
                sqlBuilder.setTables(DatabaseHelper.Tables.SMS_TABLE);
                sqlBuilder.appendWhere(Telephony.Mms._ID + "=" + sId);
                break;
            default:
                throw new IllegalArgumentException("Error Uri: " + uri);
        }
        Cursor cursor = sqlBuilder.query(db, projection, selection, selectionArgs,
                null, null, TextUtils.isEmpty(sortOrder) ? null : sortOrder, limit);
        cursor.setNotificationUri(mResolver, uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
