package com.byod.contacts.data;

import android.content.AsyncQueryHandler;
import com.byod.data.IAsyncQuery;

public class LocalContactsAsyncQuery implements IAsyncQuery{
    private final AsyncQueryHandler mAsyncQueryHandler;
    public LocalContactsAsyncQuery(AsyncQueryHandler asyncQueryHandler) {
        this.mAsyncQueryHandler = asyncQueryHandler;
    }
    @Override
    public void startQuery() {
        // TODO
    }
}
