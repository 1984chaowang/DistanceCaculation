package io.pravega.flinkapp;


import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

import java.util.concurrent.TimeUnit;

public class InfluxdbSink extends RichSinkFunction<OutSenorData> {
    InfluxDB influxDB = null;
    String influxdbUrl = "";
    String influxdbUsername = "";
    String influxdbPassword = "";
    String influxdbDbName = "";

    public InfluxdbSink() {}

    public InfluxdbSink(String influxdbUrl, String influxdbUsername, String influxdbPassword, String influxdbDbName) {
        this.influxdbUrl = influxdbUrl;
        this.influxdbUsername = influxdbUsername;
        this.influxdbPassword = influxdbPassword;
        this.influxdbDbName = influxdbDbName;
    }

    @Override
    public void invoke(OutSenorData value) {
        try {
            //String influxdbDbName = "demo";
            influxDB.query(new Query("CREATE DATABASE " + influxdbDbName));
            influxDB.setDatabase(influxdbDbName);
            System.out.println("value: " + value);
            influxDB.write(Point.measurement(value.getSensorid())
                    .time(value.getTimestamp(), TimeUnit.MILLISECONDS)
                    .addTag("")
                    .addField("DIFFERENCE", value.getDifference())
                    .addField("TREND", value.getTrend())
                    .addField("AVERAGE", value.getAverage())
                    .build());
        } catch(Exception e) {
            System.out.println("Failed!");
            e.printStackTrace();
        }
    }

    @Override
    public void open(Configuration config) {
        influxDB = InfluxDBFactory.connect(influxdbUrl, influxdbUsername, influxdbPassword);
        //influxDB = InfluxDBFactory.connect("http://192.168.188.130:8086", "root", "root");
    }

    @Override
    public void close() throws Exception {
        if (influxDB != null) {
            influxDB.close();
        }
    }
}
