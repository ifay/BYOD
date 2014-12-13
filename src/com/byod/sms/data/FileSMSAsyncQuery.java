package com.byod.sms.data;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;

import com.byod.data.IAsyncQuery;

class FileSMSAsyncQuery implements IAsyncQuery{
    private final AsyncQueryHandler mAsyncQueryHandler;
    public FileSMSAsyncQuery(AsyncQueryHandler asyncQueryHandler) {
        this.mAsyncQueryHandler = asyncQueryHandler;
    }
    @Override
    public void startQuery() {
        // TODO
    }

    @Override
    public void startDelete() {

    }

    @Override
    public void startDelete(int id) {

    }

    @Override
    public void startInsert(ContentValues values) {

    }
}
