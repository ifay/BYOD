package com.byod.contacts.data;

import android.content.AsyncQueryHandler;
import com.byod.data.IAsyncQuery;

class FileContactsAsyncQuery implements IAsyncQuery{
    private final AsyncQueryHandler mAsyncQueryHandler;
    public FileContactsAsyncQuery(AsyncQueryHandler asyncQueryHandler) {
        this.mAsyncQueryHandler = asyncQueryHandler;
    }
    @Override
    public void startQuery() {
        // TODO
    }
}
