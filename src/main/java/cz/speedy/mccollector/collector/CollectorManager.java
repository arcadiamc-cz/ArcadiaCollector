package cz.speedy.mccollector.collector;

import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CollectorManager implements Runnable {

    private final InfluxDB influxDB;
    private final List<Collector> collectors;

    public CollectorManager(InfluxDB influxDB) {
        this.influxDB = influxDB;
        this.collectors = new ArrayList<>();
    }

    public CollectorManager registerCollector(Collector collector) {
        collectors.add(collector);
        return this;
    }

    @Override
    public void run() {
        try {
            BatchPoints batchPoints = BatchPoints.builder().build();
            long millis = System.currentTimeMillis();
            collectors.forEach(collector -> {
                collector.collectAsync(influxDB, millis);
                List<Point.Builder> points = collector.collect();
                if(points != null && !points.isEmpty()) {
                    points.forEach(point -> {
                        point.time(millis, TimeUnit.MILLISECONDS);
                        batchPoints.point(point.build());
                    });
                }
            });
            influxDB.write(batchPoints);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}