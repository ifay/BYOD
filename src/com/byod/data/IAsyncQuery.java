package com.byod.data;

import android.content.ContentValues;

public interface IAsyncQuery {
    public void startQuery();
    public void startInsert(ContentValues values);
}
