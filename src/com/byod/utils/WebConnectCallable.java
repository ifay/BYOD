package com.byod.utils;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class WebConnectCallable implements Callable {
    private static String TAG = "WebConnectCallable";
    private String url;
    private String nameSpace;
    private PropertyInfo[] propertyInfo;
    private String method;
    
    public WebConnectCallable (String url, String nameSpace, String method, PropertyInfo[] propertyInfo) {
        this.nameSpace = nameSpace;
        this.url = url;
        this.propertyInfo = propertyInfo;
        this.method = method;
    }
    
    @Override
    public Object call() throws Exception {
        HttpTransportSE ht = new HttpTransportSE(url,60000);
        ht.debug = true;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        SoapObject request = new SoapObject(nameSpace, method);
        for (PropertyInfo property : propertyInfo) {
            request.addProperty(property);
        }
        envelope.bodyOut = request;
        try {
            ht.call(null, envelope);
            Object obj = envelope.getResponse();
            Log.d(TAG,method + " result is "+obj.toString());
            return obj.toString();
        } catch (HttpResponseException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (XmlPullParserException e) {
            throw e;
        }
    }

}