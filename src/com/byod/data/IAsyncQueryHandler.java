package com.byod.data;

import android.database.Cursor;
import android.net.Uri;

public interface IAsyncQueryHandler {
    void onQueryComplete(int token, Object cookie, Cursor cursor);

    void onDeleteComplete(int token, Object cookie, int result);

    void onUpdateComplete(int token, Object cookie, int result);

    void onInsertComplete(int token, Object cookie, Uri uri);
}
