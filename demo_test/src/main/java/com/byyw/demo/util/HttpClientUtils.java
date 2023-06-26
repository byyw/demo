package com.byyw.demo.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import cn.hutool.json.JSONObject;

/**
 * http模拟请求工具
 */
public class HttpClientUtils {
    private static RequestConfig config;
    private static CloseableHttpAsyncClient httpAsyncClient;
    private static CloseableHttpClient httpClient;
    static{
        config = RequestConfig.custom()
            .setConnectTimeout(15000)
            .setSocketTimeout(60000)
            .build();
        // 创建httpClient实例对象
        httpClient = HttpClients.createDefault();
        httpAsyncClient = HttpAsyncClients.createDefault();
    }

    public static void close(){
        try {
            httpClient.close();
            httpAsyncClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String sendGet(String url) throws ParseException, IOException{
        // 创建GET请求方法实例对象
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity, "UTF-8");
        response.close();
        return result;
    }

    public static String sendPost(String url,JSONObject params) throws ParseException, IOException {
        // 创建POST请求方法实例对象
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);
        httpPost.setHeader("Content-Type", "application/json");

        // List<NameValuePair> formparams = new ArrayList<>();
        // for(Entry<String, String> en : params.entrySet()){
        //     formparams.add(new BasicNameValuePair(en.getKey(), en.getValue()));
        // }
        // formparams.add(new BasicNameValuePair("type", "house"));
        // UrlEncodedFormEntity uefEntity;
        // uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
        // httpPost.setEntity(uefEntity);
        httpPost.setEntity(new StringEntity(params.toString()));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity, "UTF-8");
        response.close();
        return result;
    }
    public static String sendPost(String url,Map<String,String> params) throws ParseException, IOException {
        return sendPost(url,new JSONObject(params));
    }

    public static Future<HttpResponse> sendAsyncGet(String url){
        return sendAsyncGet(url,null);
    }
    public static Future<HttpResponse> sendAsyncGet(String url,FutureCallback<HttpResponse> futureCallback){
        // 创建httpClient实例对象
        // 创建GET请求方法实例对象
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);
        httpAsyncClient.start();
        Future<HttpResponse> future = httpAsyncClient.execute(httpGet, futureCallback);
        return future;
    }

    public static Future<HttpResponse> sendAsyncPost(String url,Map<String,String> params) throws UnsupportedEncodingException{
        return sendAsyncPost(url,new JSONObject(params),null);
    }
    public static Future<HttpResponse> sendAsyncPost(String url,JSONObject params,FutureCallback<HttpResponse> futureCallback) throws UnsupportedEncodingException{
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);
        httpPost.setHeader("Content-Type", "application/json");
        // httpPost.setEntity(new StringEntity(params.toString()));
        httpPost.setEntity(new StringEntity("null"));
        httpAsyncClient.start();
        Future<HttpResponse> future = httpAsyncClient.execute(httpPost, futureCallback);
        return future;
    }

}
