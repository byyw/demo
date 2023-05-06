package com.byyw.demo;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause.Entry;
import com.byyw.demo.mapper.GpsinfoMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.InfluxQLQuery;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.influxdb.query.InfluxQLQueryResult;

import cn.hutool.core.date.DateUtil;
import jakarta.annotation.PostConstruct;

@Component
public class TestDemo {

    @Autowired
    private GpsinfoMapper gpsinfoMapper;

    // @PostConstruct
    public void init_testbase() throws InterruptedException {
        ExecutorService executorService = new ThreadPoolExecutor(10, 10,
                0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1000)); 

        long start = DateUtil.parse("2023-03-01 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime();
        InfluxDBClient client = InfluxDBClientFactory.create("http://172.16.0.13:8086",
                "VNzBxJs42dsULEUGbCKVzXhNmC0VKYLLgOBaCRxzmGVfbm69HX_UXNM7qGn_6zwbZI5Eq7Y50vTUSvg4mcJ8UQ=="
                        .toCharArray());
        long t = System.currentTimeMillis();
        for (int i = 0; i < 100_0000; i++) {
            int ii = i;
            while (true) {
                try {
                    executorService.submit(() -> {
                        while (true) {
                            try {
                                Point p = Point.measurement("test2")
                                        .time(Instant.ofEpochMilli(start + ii * 1000), WritePrecision.MS);
                                for(int j=0;j<5;j++){
                                    p.addTag("tag"+j, "tag"+j+"_" + (int) (Math.random() * 50));
                                }
                                for(int j=0;j<100;j++){
                                    p.addField("val"+j, Math.random() * 100);
                                }
                                

                                client.getWriteApiBlocking().writePoint("testbase", "fj", p);
                                if (ii == 100_0000 - 1) {
                                    System.out.println("耗时：" + (System.currentTimeMillis() - t) + "ms");
                                }
                                break;
                            } catch (Exception e) {
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });
                    break;
                } catch (Exception e) {
                    Thread.sleep(1);
                }
            }
            if (i % 10000 == 0) {
                System.out.println("已写入：" + i);
            }
        }
    }
    
    // @PostConstruct
    public void test_write_2000() throws InterruptedException {

        ExecutorService executorService = new ThreadPoolExecutor(15, 15,
                0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1000));

        // 插入influxdb
        InfluxDBClient clientTest = InfluxDBClientFactory.create("http://172.16.0.12:8086",
                "Z6iTdqqAYuF2OHKls_O7DVlTSBfS2VNC80QjrLYa4ok0ESb-SLv1zMTWmTGfNdU-thdcTmwK69ijA9gPyydFSw=="
                        .toCharArray());

        AtomicInteger sum = new AtomicInteger(0);
        long start = DateUtil.parse("2023-03-01 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime();
        final Long t = System.currentTimeMillis();
        for(int i=0;i<2000_0000;i++){
            int ii = i;
            while (true) {
                try {
                    executorService.submit(() -> {
                        while (true) {
                            try {
                                Point p = Point.measurement("test2")
                                        .time(Instant.ofEpochMilli(start + ii * 1000), WritePrecision.MS);
                                p.addTag("tag1", "tag1_" + ii%50);
                                p.addField("val1", ii%100);

                                clientTest.getWriteApiBlocking().writePoint("testbase", "fj", p);
                                sum.addAndGet(1);
                                if (sum.get() % 100_0000 == 0) {
                                    System.out.println(sum.get() + " influxdb耗时：" + (System.currentTimeMillis() - t) + "ms");
                                }
                                break;
                            } catch (Exception e) {
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });
                    break;
                } catch (Exception e) {
                    Thread.sleep(1);
                }
            }
        }
        System.out.println("------");
        AtomicInteger sum2 = new AtomicInteger(0);
        final Long t2 = System.currentTimeMillis();
        for(int i=0;i<2000_0000;i++){
            int ii = i;
            while (true) {
                try {
                    executorService.submit(() -> {
                        while (true) {
                            try {
                                gpsinfoMapper.insertTest("insert test3 values(from_unixtime("+(start + ii * 1000)+"/1000),'"+(ii%50)+"','"+(ii%100)+"')");
                                sum2.addAndGet(1);
                                if (sum2.get() % 100_0000 == 0) {
                                    System.out.println(sum2.get() + " mysql耗时：" + (System.currentTimeMillis() - t2) + "ms");
                                }
                                break;
                            } catch (Exception e) {
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });
                    break;
                } catch (Exception e) {
                    Thread.sleep(1);
                }
            }
        }
        System.out.println("ready end...");
    }

    // @PostConstruct
    public void test_write_m2() throws InterruptedException {
        List<Map<String,Object>> list = new ArrayList<>();
        for(int i=0;i<100;i++){
            Map<String,Object> map = new HashMap<>();
            for(int j=0;j<5;j++){
                map.put("tag"+j, "tag"+j+"_" + (int) (Math.random() * 50));
            }
            for(int j=0;j<100;j++){
                map.put("val"+j, Math.random() * 100);
            }
            list.add(map);
        }


        ExecutorService executorService = new ThreadPoolExecutor(15, 15,
                0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1000));

        // 插入influxdb
        InfluxDBClient clientTest = InfluxDBClientFactory.create("http://172.16.0.12:8086",
                "Z6iTdqqAYuF2OHKls_O7DVlTSBfS2VNC80QjrLYa4ok0ESb-SLv1zMTWmTGfNdU-thdcTmwK69ijA9gPyydFSw=="
                        .toCharArray());

        AtomicInteger sum = new AtomicInteger(0);
        long start = DateUtil.parse("2023-03-01 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime();
        final Long t = System.currentTimeMillis();
        for(int i=0;i<list.size()*1000;i++){
            int ii = i;
            while (true) {
                try {
                    executorService.submit(() -> {
                        while (true) {
                            try {
                                Map<String,Object> map = list.get(ii%100);
                                Point p = Point.measurement("test5")
                                        .time(Instant.ofEpochMilli(start + ii * 1000), WritePrecision.MS);
                                for(Map.Entry<String,Object> entry : map.entrySet()){
                                    if(entry.getKey().startsWith("tag")){
                                        p.addTag(entry.getKey(), (String)entry.getValue());
                                    }else{
                                        p.addField(entry.getKey(), (Double)entry.getValue());
                                    }
                                }

                                clientTest.getWriteApiBlocking().writePoint("testbase", "fj", p);
                                sum.addAndGet(1);
                                if (sum.get() % 10_0000 == 0) {
                                    System.out.println(sum.get() + " influxdb耗时：" + (System.currentTimeMillis() - t) + "ms");
                                }
                                break;
                            } catch (Exception e) {
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });
                    break;
                } catch (Exception e) {
                    Thread.sleep(1);
                }
            }
        }
        System.out.println("------");
        AtomicInteger sum2 = new AtomicInteger(0);
        final Long t2 = System.currentTimeMillis();
        for(int i=0;i<list.size()*1000;i++){
            int ii = i;
            while (true) {
                try {
                    executorService.submit(() -> {
                        while (true) {
                            try {
                                Map<String,Object> map = list.get(ii%100);
                                String sql = "insert test5 values(from_unixtime("+(start + ii * 1000)+"/1000)";
                                for(int j=0;j<5;j++){
                                    sql += ",'" + map.get("tag"+j) + "'";
                                }
                                for(int j=0;j<100;j++){
                                    sql += ",'" + (map.get("val"+j)+"").substring(0,10) + "'";
                                }
                                sql += ")";
                                gpsinfoMapper.insertTest(sql);
                                sum2.addAndGet(1);
                                if (sum2.get() % 10_0000 == 0) {
                                    System.out.println(sum2.get() + " mysql耗时：" + (System.currentTimeMillis() - t2) + "ms");
                                }
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });
                    break;
                } catch (Exception e) {
                    Thread.sleep(1);
                }
            }
        }
        System.out.println("ready end...");
    }

    // @PostConstruct
    public void test_write() throws InterruptedException {

        InfluxDBClient client = InfluxDBClientFactory.create("http://172.16.0.13:8086",
                "VNzBxJs42dsULEUGbCKVzXhNmC0VKYLLgOBaCRxzmGVfbm69HX_UXNM7qGn_6zwbZI5Eq7Y50vTUSvg4mcJ8UQ=="
                        .toCharArray());

        String sql = """
                SELECT * FROM "test2" limit 100000
                    """;

        InfluxQLQueryResult result = client.getInfluxQLQueryApi()
                .query(new InfluxQLQuery(sql, "testbase").setPrecision(InfluxQLQuery.InfluxQLPrecision.MILLISECONDS));

        ExecutorService executorService = new ThreadPoolExecutor(15, 15,
                0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1000));

        // 插入influxdb
        InfluxDBClient clientTest = InfluxDBClientFactory.create("http://172.16.0.12:8086",
                "Z6iTdqqAYuF2OHKls_O7DVlTSBfS2VNC80QjrLYa4ok0ESb-SLv1zMTWmTGfNdU-thdcTmwK69ijA9gPyydFSw=="
                        .toCharArray());

        System.out.println("size = " + result.getResults().get(0).getSeries().get(0).getValues().size());

        AtomicInteger sum = new AtomicInteger(0);
        final Long t = System.currentTimeMillis();
        for (InfluxQLQueryResult.Result resultResult : result.getResults()) {
            for (InfluxQLQueryResult.Series series : resultResult.getSeries()) {
                for (InfluxQLQueryResult.Series.Record record : series.getValues()) {

                    while (true) {
                        try {
                            executorService.submit(() -> {
                                while (true) {
                                    try {
                                        Point p = Point.measurement("test3")
                                                .time(Instant
                                                        .ofEpochMilli(Long.valueOf((String) record.getValues()[0])),
                                                        WritePrecision.MS);
                                        p.addTag("tag1", (String) record.getValues()[1]);
                                        p.addField("val1", Double.valueOf((String) record.getValues()[2]));

                                        clientTest.getWriteApiBlocking().writePoint("testbase", "fj", p);
                                        sum.addAndGet(1);
                                        if (sum.get() % 10_0000 == 0) {
                                            System.out.println(
                                                    sum.get() + " influxdb耗时：" + (System.currentTimeMillis() - t) + "ms");
                                        }
                                        if (sum.get() == series.getValues().size()) {
                                            System.out.println(
                                                    sum.get() + " influxdb耗时：" + (System.currentTimeMillis() - t) + "ms");
                                        }
                                        break;
                                    } catch (Exception e) {
                                        try {
                                            Thread.sleep(10);
                                        } catch (InterruptedException e1) {
                                            // TODO Auto-generated catch block
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                            });
                            break;
                        } catch (Exception e) {
                            Thread.sleep(1);
                        }
                    }
                }
            }
        }
        System.out.println("------");
        AtomicInteger sum2 = new AtomicInteger(0);
        final Long t2 = System.currentTimeMillis();
        for (InfluxQLQueryResult.Result resultResult : result.getResults()) {
            for (InfluxQLQueryResult.Series series : resultResult.getSeries()) {
                for (InfluxQLQueryResult.Series.Record record : series.getValues()) {
                    while (true) {
                        try {
                            executorService.submit(() -> {
                                while (true) {
                                    try {
                                        gpsinfoMapper.insertTest("insert test3 values(from_unixtime("+record.getValues()[0]+"/1000),'"+record.getValues()[1]+"','"+record.getValues()[2]+"')");
                                        sum2.addAndGet(1);
                                        if (sum2.get() % 10_0000 == 0) {
                                            System.out.println(
                                                    sum2.get() + " mysql耗时：" + (System.currentTimeMillis() - t2) + "ms");
                                        }
                                        if (sum2.get() == series.getValues().size()) {
                                            System.out.println(
                                                    sum2.get() + " mysql耗时：" + (System.currentTimeMillis() - t2) + "ms");
                                        }
                                        break;
                                    } catch (Exception e) {
                                        try {
                                            Thread.sleep(10);
                                        } catch (InterruptedException e1) {
                                            // TODO Auto-generated catch block
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                            });
                            break;
                        } catch (Exception e) {
                            Thread.sleep(1);
                        }
                    }
                }
            }
        }
        System.out.println("ready end...");
    }

    // @PostConstruct
    public void test_read() throws InterruptedException {
        String database = "test1";

        InfluxDBClient clientTest = InfluxDBClientFactory.create("http://172.16.0.12:8086",
                "Z6iTdqqAYuF2OHKls_O7DVlTSBfS2VNC80QjrLYa4ok0ESb-SLv1zMTWmTGfNdU-thdcTmwK69ijA9gPyydFSw==".toCharArray());

        InfluxQLQueryResult result;
        List<Map<String,Object>> list;
        long t;
        String sql;


        System.out.println("查询全部：");
        sql = "SELECT * FROM \""+database+"\"";
        t = System.currentTimeMillis();
        result = clientTest.getInfluxQLQueryApi()
                .query(new InfluxQLQuery(sql, "testbase").setPrecision(InfluxQLQuery.InfluxQLPrecision.MILLISECONDS));
        System.out.println("influxdb:"+result.getResults().get(0).getSeries().get(0).getValues().size()+"用时:"+(System.currentTimeMillis()-t));
        t = System.currentTimeMillis();
        list = gpsinfoMapper.selectList("select * from "+database);
        System.out.println("mysql:"+list.size()+"用时:"+(System.currentTimeMillis()-t));
        System.out.println();


        System.out.println("查询tag1=tag1_0：");
        sql = "select * from \""+database+"\" where tag1='tag1_0'";
        t = System.currentTimeMillis();
        result = clientTest.getInfluxQLQueryApi()
                .query(new InfluxQLQuery(sql, "testbase").setPrecision(InfluxQLQuery.InfluxQLPrecision.MILLISECONDS));
        System.out.println("influxdb:"+result.getResults().get(0).getSeries().get(0).getValues().size()+"用时:"+(System.currentTimeMillis()-t));
        t = System.currentTimeMillis();
        list = gpsinfoMapper.selectList("select * from "+database+" where tag1='tag1_0'");
        System.out.println("mysql:"+list.size()+"用时:"+(System.currentTimeMillis()-t));
        System.out.println();

        
        System.out.println("查询tag1=tag1_1，每5分钟平均值：");
        sql = "select MEAN(\"val1\") AS \"val1\", MIN(\"time\") AS \"minTime\", MAX(\"time\") AS \"maxTime\" from \""+database+"\" where tag1='tag1_1' group by time(5m) fill(none)";
        t = System.currentTimeMillis();
        result = clientTest.getInfluxQLQueryApi()
                .query(new InfluxQLQuery(sql, "testbase").setPrecision(InfluxQLQuery.InfluxQLPrecision.MILLISECONDS));
        System.out.println("influxdb:"+result.getResults().get(0).getSeries().get(0).getValues().size()+"用时:"+(System.currentTimeMillis()-t));
        t = System.currentTimeMillis();
        list = gpsinfoMapper.selectList("SELECT min(time),max(time),any_value(tag1),avg(val1) FROM "+database+" WHERE tag1 = 'tag1_1' GROUP BY DATE( time ), HOUR ( time ), ((MINUTE( time ) div 5)*5)");
        System.out.println("mysql:"+list.size()+"用时:"+(System.currentTimeMillis()-t));
        System.out.println();

        
        System.out.println("查询tag1=tag1_2，每1小时最大值：");
        sql = "select MAX(\"val1\") AS \"val1\", MIN(\"time\") AS \"minTime\", MAX(\"time\") AS \"maxTime\" from \""+database+"\" where tag1='tag1_2' group by time(1h) fill(none)";
        t = System.currentTimeMillis();
        result = clientTest.getInfluxQLQueryApi()
                .query(new InfluxQLQuery(sql, "testbase").setPrecision(InfluxQLQuery.InfluxQLPrecision.MILLISECONDS));
        System.out.println("influxdb:"+result.getResults().get(0).getSeries().get(0).getValues().size()+"用时:"+(System.currentTimeMillis()-t));
        t = System.currentTimeMillis();
        list = gpsinfoMapper.selectList("SELECT min(time),max(time),any_value(tag1),max(val1) FROM "+database+" WHERE tag1 = 'tag1_2' GROUP BY DATE( time ), HOUR ( time )");
        System.out.println("mysql:"+list.size()+"用时:"+(System.currentTimeMillis()-t));
        System.out.println();
        

        System.out.println("查询tag1=tag1_3，每天总和：");
        sql = "select MAX(\"val1\") AS \"val1\", MIN(\"time\") AS \"minTime\", MAX(\"time\") AS \"maxTime\" from \""+database+"\" where tag1='tag1_3' group by time(1d) fill(none)";
        t = System.currentTimeMillis();
        result = clientTest.getInfluxQLQueryApi()
                .query(new InfluxQLQuery(sql, "testbase").setPrecision(InfluxQLQuery.InfluxQLPrecision.MILLISECONDS));
        System.out.println("influxdb:"+result.getResults().get(0).getSeries().get(0).getValues().size()+"用时:"+(System.currentTimeMillis()-t));
        t = System.currentTimeMillis();
        list = gpsinfoMapper.selectList("SELECT min(time),max(time),any_value(tag1),sum(val1) FROM "+database+" WHERE tag1 = 'tag1_3' GROUP BY DATE( time )");
        System.out.println("mysql:"+list.size()+"用时:"+(System.currentTimeMillis()-t));
        System.out.println();

        
        System.out.println("查询tag1=tag1_4，val1>20：");
        sql = "select * from \""+database+"\" where tag1='tag1_4' and val1>20";
        t = System.currentTimeMillis();
        result = clientTest.getInfluxQLQueryApi()
                .query(new InfluxQLQuery(sql, "testbase").setPrecision(InfluxQLQuery.InfluxQLPrecision.MILLISECONDS));
        System.out.println("influxdb:"+result.getResults().get(0).getSeries().get(0).getValues().size()+"用时:"+(System.currentTimeMillis()-t));
        t = System.currentTimeMillis();
        list = gpsinfoMapper.selectList("SELECT * FROM "+database+" WHERE tag1 = 'tag1_4' and val1>20");
        System.out.println("mysql:"+list.size()+"用时:"+(System.currentTimeMillis()-t));
        System.out.println();

        
        System.out.println("查询tag1=tag1_0 or tag1=tag1_3：");
        sql = "select * from \""+database+"\" where tag1='tag1_0' or tag1='tag1_3'";
        t = System.currentTimeMillis();
        result = clientTest.getInfluxQLQueryApi()
                .query(new InfluxQLQuery(sql, "testbase").setPrecision(InfluxQLQuery.InfluxQLPrecision.MILLISECONDS));
        System.out.println("influxdb:"+result.getResults().get(0).getSeries().get(0).getValues().size()+"用时:"+(System.currentTimeMillis()-t));
        t = System.currentTimeMillis();
        list = gpsinfoMapper.selectList("SELECT * FROM "+database+" WHERE tag1='tag1_0' or tag1='tag1_3'");
        System.out.println("mysql:"+list.size()+"用时:"+(System.currentTimeMillis()-t));
        System.out.println();
        
        
        System.out.println("查询2023-03-01 00:00:00<time<2023-03-01 12:00:00：");
        sql = "select * from \""+database+"\" where '2023-03-01 00:00:00'<time and time<'2023-03-01 12:00:00'";
        t = System.currentTimeMillis();
        result = clientTest.getInfluxQLQueryApi()
                .query(new InfluxQLQuery(sql, "testbase").setPrecision(InfluxQLQuery.InfluxQLPrecision.MILLISECONDS));
        System.out.println("influxdb:"+result.getResults().get(0).getSeries().get(0).getValues().size()+"用时:"+(System.currentTimeMillis()-t));
        t = System.currentTimeMillis();
        list = gpsinfoMapper.selectList("SELECT * FROM "+database+" WHERE '2023-03-01 00:00:00'<time and time<'2023-03-01 12:00:00'");
        System.out.println("mysql:"+list.size()+"用时:"+(System.currentTimeMillis()-t));
        System.out.println();

        
    }

    @PostConstruct
    public void test_read2() throws InterruptedException {
        String database = "test5";

        InfluxDBClient clientTest = InfluxDBClientFactory.create("http://172.16.0.12:8086",
                "Z6iTdqqAYuF2OHKls_O7DVlTSBfS2VNC80QjrLYa4ok0ESb-SLv1zMTWmTGfNdU-thdcTmwK69ijA9gPyydFSw==".toCharArray());

        InfluxQLQueryResult result;
        List<Map<String,Object>> list;
        long t;
        String sql;


        System.out.println("查询全部：");
        // sql = "SELECT * FROM \""+database+"\"";
        // t = System.currentTimeMillis();
        // result = clientTest.getInfluxQLQueryApi()
        //         .query(new InfluxQLQuery(sql, "testbase").setPrecision(InfluxQLQuery.InfluxQLPrecision.MILLISECONDS));
        // System.out.println("influxdb:"+result.getResults().get(0).getSeries().get(0).getValues().size()+"用时:"+(System.currentTimeMillis()-t));
        // t = System.currentTimeMillis();
        // list = gpsinfoMapper.selectList("select * from "+database);
        // System.out.println("mysql:"+list.size()+"用时:"+(System.currentTimeMillis()-t));
        // System.out.println();


        // System.out.println("查询tag1 = tag1_0，tag2 = tag2_0：");
        // sql = "select * from \""+database+"\" where tag1='tag1_0' or tag2='tag2_0'";
        // t = System.currentTimeMillis();
        // result = clientTest.getInfluxQLQueryApi()
        //         .query(new InfluxQLQuery(sql, "testbase").setPrecision(InfluxQLQuery.InfluxQLPrecision.MILLISECONDS));
        // System.out.println("influxdb:"+result.getResults().get(0).getSeries().get(0).getValues().size()+"用时:"+(System.currentTimeMillis()-t));
        // t = System.currentTimeMillis();
        // list = gpsinfoMapper.selectList("select * from "+database+" where tag1='tag1_0' or tag2='tag2_0'");
        // System.out.println("mysql:"+list.size()+"用时:"+(System.currentTimeMillis()-t));
        // System.out.println();

        
        // System.out.println("查询tag1 = tag1_1，tag2 = tag2_1，每5分钟平均值：");
        // sql = "select MEAN(\"val1\") AS \"val1\", MIN(\"time\") AS \"minTime\", MAX(\"time\") AS \"maxTime\" from \""+database+"\" where tag1='tag1_1' or tag2='tag2_1' group by time(5m) fill(none)";
        // t = System.currentTimeMillis();
        // result = clientTest.getInfluxQLQueryApi()
        //         .query(new InfluxQLQuery(sql, "testbase").setPrecision(InfluxQLQuery.InfluxQLPrecision.MILLISECONDS));
        // System.out.println("influxdb:"+result.getResults().get(0).getSeries().get(0).getValues().size()+"用时:"+(System.currentTimeMillis()-t));
        // t = System.currentTimeMillis();
        // list = gpsinfoMapper.selectList("SELECT min(time),max(time),any_value(tag1),avg(val1) FROM "+database+" WHERE tag1 = 'tag1_1' or tag2='tag2_1' GROUP BY DATE( time ), HOUR ( time ), ((MINUTE( time ) div 5)*5)");
        // System.out.println("mysql:"+list.size()+"用时:"+(System.currentTimeMillis()-t));
        // System.out.println();

        
        // System.out.println("查询tag1 = tag1_2，tag2 = tag2_2，每1小时最大值：");
        // sql = "select MAX(\"val1\") AS \"val1\", MIN(\"time\") AS \"minTime\", MAX(\"time\") AS \"maxTime\" from \""+database+"\" where tag1='tag1_2' or tag2='tag2_2' group by time(1h) fill(none)";
        // t = System.currentTimeMillis();
        // result = clientTest.getInfluxQLQueryApi()
        //         .query(new InfluxQLQuery(sql, "testbase").setPrecision(InfluxQLQuery.InfluxQLPrecision.MILLISECONDS));
        // System.out.println("influxdb:"+result.getResults().get(0).getSeries().get(0).getValues().size()+"用时:"+(System.currentTimeMillis()-t));
        // t = System.currentTimeMillis();
        // list = gpsinfoMapper.selectList("SELECT min(time),max(time),any_value(tag1),max(val1) FROM "+database+" WHERE tag1 = 'tag1_2' or tag2='tag2_2' GROUP BY DATE( time ), HOUR ( time )");
        // System.out.println("mysql:"+list.size()+"用时:"+(System.currentTimeMillis()-t));
        // System.out.println();
        

        // System.out.println("查询tag1 = tag1_3，tag2 = tag2_3，每天总和：");
        // sql = "select MAX(\"val1\") AS \"val1\", MIN(\"time\") AS \"minTime\", MAX(\"time\") AS \"maxTime\" from \""+database+"\" where tag1='tag1_3' or tag2='tag2_3' group by time(1d) fill(none)";
        // t = System.currentTimeMillis();
        // result = clientTest.getInfluxQLQueryApi()
        //         .query(new InfluxQLQuery(sql, "testbase").setPrecision(InfluxQLQuery.InfluxQLPrecision.MILLISECONDS));
        // System.out.println("influxdb:"+result.getResults().get(0).getSeries().get(0).getValues().size()+"用时:"+(System.currentTimeMillis()-t));
        // t = System.currentTimeMillis();
        // list = gpsinfoMapper.selectList("SELECT min(time),max(time),any_value(tag1),sum(val1) FROM "+database+" WHERE tag1 = 'tag1_3' or tag2='tag2_3' GROUP BY DATE( time )");
        // System.out.println("mysql:"+list.size()+"用时:"+(System.currentTimeMillis()-t));
        // System.out.println();

        
        // System.out.println("查询tag1 = tag1_4，tag2 = tag2_4，val1>20，val2<10：");
        // sql = "select * from \""+database+"\" where (tag1='tag1_4' or tag2='tag2_4') and val1>20 and val2<10";
        // t = System.currentTimeMillis();
        // result = clientTest.getInfluxQLQueryApi()
        //         .query(new InfluxQLQuery(sql, "testbase").setPrecision(InfluxQLQuery.InfluxQLPrecision.MILLISECONDS));
        // System.out.println("influxdb:"+result.getResults().get(0).getSeries().get(0).getValues().size()+"用时:"+(System.currentTimeMillis()-t));
        // t = System.currentTimeMillis();
        // list = gpsinfoMapper.selectList("SELECT * FROM "+database+" WHERE (tag1 = 'tag1_4' or tag2='tag2_4') and val1>20 and val2<10");
        // System.out.println("mysql:"+list.size()+"用时:"+(System.currentTimeMillis()-t));
        // System.out.println();
        
        
        System.out.println("查询2023-03-01 00:00:00<time<2023-03-01 12:00:00：");
        sql = "select * from \""+database+"\" where '2023-03-01 00:00:00'<time and time<'2023-03-01 12:00:00'";
        t = System.currentTimeMillis();
        result = clientTest.getInfluxQLQueryApi()
                .query(new InfluxQLQuery(sql, "testbase").setPrecision(InfluxQLQuery.InfluxQLPrecision.MILLISECONDS));
        System.out.println("influxdb:"+result.getResults().get(0).getSeries().get(0).getValues().size()+"用时:"+(System.currentTimeMillis()-t));
        t = System.currentTimeMillis();
        list = gpsinfoMapper.selectList("SELECT * FROM "+database+" WHERE '2023-03-01 00:00:00'<time and time<'2023-03-01 12:00:00'");
        System.out.println("mysql:"+list.size()+"用时:"+(System.currentTimeMillis()-t));
        System.out.println();

        
    }

}
