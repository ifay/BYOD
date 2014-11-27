package com.byod.contacts.data;

import android.content.AsyncQueryHandler;
import com.byod.data.IAsyncQuery;

public class OnlineContactsAsyncQuery implements IAsyncQuery{
    private final AsyncQueryHandler mAsyncQueryHandler;
    public OnlineContactsAsyncQuery(AsyncQueryHandler asyncQueryHandler) {
        this.mAsyncQueryHandler = asyncQueryHandler;
    }
    @Override
    public void startQuery() {
        // TODO
    }
}
