package com.byod.data;

import android.content.ContentValues;

public interface IAsyncQuery {
    void startQuery();
    void startDelete();
    void startDelete(int id);
    void startInsert(ContentValues values);
}
