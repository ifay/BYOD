package com.byod.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

    public static String createJsonString(String key, Object value) {
        //创建  
        JSONObject json=new JSONObject();
        try {
            json.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
