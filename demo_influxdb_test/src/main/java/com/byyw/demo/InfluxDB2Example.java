package com.byyw.demo;

import java.time.Instant;
import java.util.List;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

public class InfluxDB2Example {

    private static InfluxDBClient client = null;

    // You can generate an API token from the "API Tokens Tab" in the UI
    private static String token = "2VhtVufOXzYBJCIq4iNtfbm2kWCGD8ay7hPKZVda74VXKo3WmV1GAoF0Cn2V3TujCDiONgXgKMLhinm8eEckKA==";
    private static String bucket = "test";
    private static String org = "byyw";

    public static void main(final String[] args) {
        client = InfluxDBClientFactory.create("http://192.168.1.77:8086", token.toCharArray());
        queryByFlux();
    }

    public static InfluxDBClient connect() {
        return InfluxDBClientFactory.create("http://192.168.1.77:8086", token.toCharArray());
    }

    public static void writeByLine(InfluxDBClient client, String data) {
        // String data = "mem,host=host1 used_percent=23.43234543";

        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writeRecord(bucket, org, WritePrecision.NS, data);

    }

    public static void writeByPoint(String bucket,InfluxDBClient client,Point point) {
        // Point point = Point
        // .measurement("mem")
        // .addTag("host", "host1")
        // .addField("used_percent", 23.43234543)
        // .time(Instant.now(), WritePrecision.NS);

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

    public static void queryByFlux() {
        String flux = """
                    from(bucket: "test")
                    |> range(start: 2010-01-01T00:00:00.000Z, stop: 2023-01-01T00:00:00.000Z)
                    |> filter(fn: (r) => r["_measurement"] == "home")
                """;

        List<FluxTable> tables = client.getQueryApi().query(flux, org);
        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                record.getValues().forEach((key, value) -> System.out.println(key + " : " +value));
                System.out.println();
            }
        }
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
}
