package com.byyw.demo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class ReadJson {
    public static void main(String[] args) throws Exception {
        
        FileInputStream fis = new FileInputStream("C:/Users/1/Desktop/dw.hzfjzn.com.har");
        FileOutputStream fos = new FileOutputStream("tt");
        Scanner sc = new Scanner(fis, "utf-8");
        StringBuffer sb = new StringBuffer();
        while(sc.hasNext()){
            sb.append(sc.nextLine());
        }
        System.out.println("re");
        JSONObject jo = JSONUtil.parseObj(sb.toString());
        jo = jo.getJSONObject("log");
        JSONArray ja = jo.getJSONArray("entries");
        for(int i=0;i<ja.size();i++){
            jo = ja.getJSONObject(i);
            if(jo.containsKey("_webSocketMessages")){
                JSONArray ja2 = jo.getJSONArray("_webSocketMessages");
                for(int j=0;j<ja2.size();j++){
                    jo = ja2.getJSONObject(j);
                    if(jo.containsKey("data")){
                        fos.write(jo.getStr("data").getBytes());
                    }
                }
            }
        }
        System.out.println("end");
    }
}
