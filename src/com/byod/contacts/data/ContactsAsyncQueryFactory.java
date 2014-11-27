package com.byod.contacts.data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import com.byod.data.IAsyncQuery;
import com.byod.data.IAsyncQueryFactory;
import com.byod.data.IAsyncQueryHandler;
import com.byod.data.MyAsyncQueryHandler;

public class ContactsAsyncQueryFactory implements IAsyncQueryFactory {
    private final AsyncQueryHandler mAsyncQueryHandler;
    public ContactsAsyncQueryFactory(ContentResolver contentResolver,
                                     IAsyncQueryHandler asyncQueryHandler) {
        this.mAsyncQueryHandler = new MyAsyncQueryHandler(contentResolver, asyncQueryHandler);
    }

    @Override
    public IAsyncQuery getFileAsyncQuery() {
        return new FileContactsAsyncQuery(mAsyncQueryHandler);
    }

    @Override
    public IAsyncQuery getSystemAsyncQuery() {
        return new SystemContactsAsyncQuery(mAsyncQueryHandler);
    }

    @Override
    public IAsyncQuery getLocalAsyncQuery() {
        return new LocalContactsAsyncQuery(mAsyncQueryHandler);
    }

    @Override
    public IAsyncQuery getOnlineAsyncQuery() {
        return new OnlineContactsAsyncQuery(mAsyncQueryHandler);
    }
}
