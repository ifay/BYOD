package com.byod.data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class MyAsyncQueryHandler extends AsyncQueryHandler{
    private final IAsyncQueryHandler mAsyncQueryHandler;
    public MyAsyncQueryHandler(ContentResolver cr,
                               IAsyncQueryHandler asyncQueryHandler) {
        super(cr);
        this.mAsyncQueryHandler = asyncQueryHandler;
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        mAsyncQueryHandler.onQueryComplete(token, cookie, cursor);
    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {
        mAsyncQueryHandler.onDeleteComplete(token, cookie, result);
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        mAsyncQueryHandler.onUpdateComplete(token, cookie, result);
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        mAsyncQueryHandler.onInsertComplete(token, cookie, uri);
    }
}
