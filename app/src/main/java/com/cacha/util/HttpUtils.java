package com.cacha.util;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/7/13.
 */

public class HttpUtils {

    /**
     * 向服务器发送post请求，包含一些Map参数
     * @param address
     * @param callback
     * @param map
     */

    public static void post(String address, okhttp3.Callback callback, Map<String, String> map)
    {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        if (map!=null)
        {
            //添加POST中传送过去的一些键值对信息
            for (Map.Entry<String, String> entry:map.entrySet())
            {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }



    /**
     * 向服务器发送json数据
     * @param address 地址
     * @param callback 回调
     * @param jsonStr json数据
     */
    public static void postJson(String address, okhttp3.Callback callback, String jsonStr)
    {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
}