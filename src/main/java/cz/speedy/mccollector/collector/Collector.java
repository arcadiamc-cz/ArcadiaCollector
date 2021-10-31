package cz.speedy.mccollector.collector;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;

import java.util.List;

public interface Collector {

    default List<Point.Builder> collect() {
        return null;
    }

    default void collectAsync(InfluxDB influxDB, long millis) {
    }
}
