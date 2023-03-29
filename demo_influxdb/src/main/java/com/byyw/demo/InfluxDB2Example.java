package com.byyw.demo;

import java.time.Instant;
import java.util.List;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.InfluxQLQuery;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.influxdb.query.InfluxQLQueryResult;

public class InfluxDB2Example {

    private static InfluxDBClient client = null;

    // You can generate an API token from the "API Tokens Tab" in the UI
    private static String token = "89DkhHDlZwxBcyaep5hITHBhdObIcz_itvEeL_8oYd-JHFFSAeYkavOU7LW4ztDZ1o4LRmIwg4R4gHZsL0XR9A==";
    private static String bucket = "test";
    private static String org = "fj";
    
    public static void main(final String[] args) {
        client = InfluxDBClientFactory.create("http://192.168.1.77:8086", token.toCharArray());
        queryByFlux();
    }

    public static void writeByLine() {
        String data = "mem,host=host1 used_percent=23.43234543";

        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writeRecord(bucket, org, WritePrecision.NS, data);

    }

    public static void writeByPoint() {
        Point point = Point
                .measurement("mem")
                .addTag("host", "host1")
                .addField("used_percent", 23.43234543)
                .time(Instant.now(), WritePrecision.NS);

        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writePoint(bucket, org, point);
    }

    public static void writeByPojo() {
        Mem mem = new Mem();
        mem.host = "host1";
        mem.used_percent = 23.43234543;

        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, mem);
    }

    @Measurement(name = "mem")
    public static class Mem {
        @Column(tag = true)
        String host;
        @Column
        Double used_percent;
        @Column(timestamp = true)
        Instant time;
    }

    public static void queryByFlux() {
        // String flux = """
        //     from(bucket:\"test\") 
        //     |> range(start: 2010-01-01T00:00:00.000Z, stop: 2023-01-01T00:00:00.000Z)
        //     |> filter(fn: (r) => r[\"_measurement\"] == \"home\")
        // """;
        String flux = """
            from(bucket: "test")
            |> range(start: 2010-01-01T00:00:00.000Z, stop: 2023-01-01T00:00:00.000Z)
            |> filter(fn: (r) => r["_measurement"] == "home")
        """;

        List<FluxTable> tables = client.getQueryApi().query(flux, org);
        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                
                if(record.getValue().equals("value")){
                    System.out.println((Double)record.getValue()+" "+record.getTime());
                }
                // record.getValues().forEach((key, value) -> System.out.println(key + " : " + value));
                // System.out.println();
            }
        }
    }

    public static void queryByInfluxQL() {
        String sql = """
            SELECT * FROM location
                """;

        InfluxQLQueryResult result = client.getInfluxQLQueryApi().query(new InfluxQLQuery(sql, "test").setPrecision(InfluxQLQuery.InfluxQLPrecision.SECONDS));

        for (InfluxQLQueryResult.Result resultResult : result.getResults()) {
            for (InfluxQLQueryResult.Series series : resultResult.getSeries()) {
                for (InfluxQLQueryResult.Series.Record record : series.getValues()) {
                    System.out.println(record.getValueByKey("time") + ": " + record.getValueByKey("first"));
                }
            }
        }
    }

}
