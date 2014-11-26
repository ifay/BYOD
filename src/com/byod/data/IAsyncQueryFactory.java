package com.byod.data;

public interface IAsyncQueryFactory {
    public IAsyncQuery getFileAsyncQuery();
    public IAsyncQuery getSystemAsyncQuery();
    public IAsyncQuery getLocalAsyncQuery();
    public IAsyncQuery getOnlineAsyncQuery();
}
