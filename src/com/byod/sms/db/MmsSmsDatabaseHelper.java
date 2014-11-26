/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.byod.sms.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MmsSmsDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "MmsSmsDatabaseHelper";

    private static MmsSmsDatabaseHelper sInstance = null;
    static final String DATABASE_NAME = "mmssms.db";
    static final int DATABASE_VERSION = 0;
    private final Context mContext;

    private MmsSmsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /**
     * Return a singleton helper for the combined MMS and SMS
     * database.
     */
    public static synchronized MmsSmsDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MmsSmsDatabaseHelper(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion
                + " to " + currentVersion + ".");
    }
}
