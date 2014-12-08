package com.byod.sms.data;

import android.content.AsyncQueryHandler;
import android.content.Context;

import com.byod.data.IAsyncQuery;
import com.byod.data.IAsyncQueryFactory;
import com.byod.data.IAsyncQueryHandler;
import com.byod.data.MyAsyncQueryHandler;

public class SMSAsyncQueryFactory implements IAsyncQueryFactory {
    private final AsyncQueryHandler mAsyncQueryHandler;
    private final Context mContext;
    public SMSAsyncQueryFactory(Context context,
                                IAsyncQueryHandler asyncQueryHandler) {
        mContext = context.getApplicationContext();
        this.mAsyncQueryHandler = new MyAsyncQueryHandler(mContext.getContentResolver(), asyncQueryHandler);
    }

    @Override
    public IAsyncQuery getFileAsyncQuery() {
        return new FileSMSAsyncQuery(mAsyncQueryHandler);
    }

    @Override
    public IAsyncQuery getSystemAsyncQuery() {
        return new SystemSMSAsyncQuery(mContext, mAsyncQueryHandler);
    }

    @Override
    public IAsyncQuery getLocalAsyncQuery() {
        return new LocalSMSAsyncQuery(mAsyncQueryHandler);
    }

    @Override
    public IAsyncQuery getOnlineAsyncQuery() {
        return new OnlineSMSAsyncQuery(mAsyncQueryHandler);
    }
}
