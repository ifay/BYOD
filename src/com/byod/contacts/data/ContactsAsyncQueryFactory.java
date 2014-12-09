package com.byod.contacts.data;

import android.content.AsyncQueryHandler;
import android.content.Context;

import com.byod.data.IAsyncQuery;
import com.byod.data.IAsyncQueryFactory;
import com.byod.data.IAsyncQueryHandler;
import com.byod.data.MyAsyncQueryHandler;

public class ContactsAsyncQueryFactory implements IAsyncQueryFactory {
    private final AsyncQueryHandler mAsyncQueryHandler;
    private final Context mContext;
    public ContactsAsyncQueryFactory(Context context,
                                     IAsyncQueryHandler asyncQueryHandler) {
        mContext = context;
        this.mAsyncQueryHandler = new MyAsyncQueryHandler(mContext.getContentResolver(), asyncQueryHandler);
    }

    @Override
    public IAsyncQuery getFileAsyncQuery() {
        return new FileContactsAsyncQuery(mAsyncQueryHandler);
    }

    @Override
    public IAsyncQuery getSystemAsyncQuery() {
        return new SystemContactsAsyncQuery(mContext, mAsyncQueryHandler);
    }

    @Override
    public IAsyncQuery getLocalAsyncQuery() {
        return new LocalContactsAsyncQuery(mContext, mAsyncQueryHandler);
    }

    @Override
    public IAsyncQuery getOnlineAsyncQuery() {
        return new OnlineContactsAsyncQuery(mAsyncQueryHandler);
    }
}
