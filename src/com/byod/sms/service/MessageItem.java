package com.byod.sms.service;

public class MessageItem {
    private int id;
    private int type;
    private int protocol;
    private String phone;
    private String body;
    private int read;
    private int threadId;
    private long date;

    public MessageItem()
    {}

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public int getProtocol()
    {
        return protocol;
    }

    public void setProtocol(int protocol)
    {
        this.protocol = protocol;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public String toString()
    {
        return "id = " + id + ";" + "type = " + type + ";" +
                "protocol = " + protocol + ";" +
                        "phone = " + phone + ";" +
                        "body = " + body;
    }


    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public long getDate() {
        return date;
    }

    public int getThreadId() {
        return threadId;
    }

    public int getRead() {
        return read;
    }
}
