package com.byyw.demo.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.byyw.demo.config.MyTableNameHandler;
import com.byyw.demo.mapper.GpsinfoMapper;
import com.byyw.demo.po.Gpsinfo;
import com.byyw.demo.util.HttpClientUtils;

import cn.hutool.json.JSONObject;
import jakarta.annotation.PostConstruct;

@Component
@RestController
public class TestDemo {

    @Autowired
    private GpsinfoMapper gpsinfoMapper;
    private static ExecutorService executorService = new ThreadPoolExecutor(20, 1000,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    @PostConstruct
    public void test() {
        for (int i = 18; i <= 19; i++) {
            String tablename = "gpsinfo_202308" + i;
            // String tablename = "gpsinfo_20230821";
            MyTableNameHandler.set(tablename);
            List<Gpsinfo> gs = gpsinfoMapper
                    .selectList(new QueryWrapper<Gpsinfo>().apply("addr is null and latitude<>0"));

            int j = 0;
            for (Gpsinfo g : gs) {
                if (g.getLatitude() != null && g.getLatitude() != 0) {
                    int jj = j;
                    executorService.execute(() -> {
                        String url = "https://restapi.amap.com/v3/geocode/regeo?key=4532a5b89bdb286c930847267ae62ed3&location="
                                + g.getGlng() + "," + g.getGlat();
                        try {
                            String res = HttpClientUtils.sendGet(url);
                            JSONObject jo = new JSONObject(res);
                            String addr = jo.getJSONObject("regeocode").getStr("formatted_address");

                            MyTableNameHandler.set(tablename);
                            gpsinfoMapper.update(null,
                                    new UpdateWrapper<Gpsinfo>().set("addr", addr).eq("id", g.getId()));
                        } catch (ParseException | IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println(tablename+" : "+jj + " / " + gs.size() );
                    });
                }
                j++;
            }
        }

    }

}
