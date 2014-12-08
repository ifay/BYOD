package com.byod.sms.data;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;

import com.byod.data.IAsyncQuery;

public class OnlineSMSAsyncQuery implements IAsyncQuery{
    private final AsyncQueryHandler mAsyncQueryHandler;
    public OnlineSMSAsyncQuery(AsyncQueryHandler asyncQueryHandler) {
        this.mAsyncQueryHandler = asyncQueryHandler;
    }
    @Override
    public void startQuery() {
        // TODO
    }

    @Override
    public void startInsert(ContentValues values) {

    }
}
