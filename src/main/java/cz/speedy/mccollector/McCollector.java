package cz.speedy.mccollector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cz.speedy.mccollector.collector.CollectorManager;
import cz.speedy.mccollector.collector.minecraft.StatusCollector;
import cz.speedy.mccollector.config.Config;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class McCollector {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) throws IOException {
        Config config = Config.loadConfig("config.json");

        InfluxDB influxDB = InfluxDBFactory.connect(config.getInfluxDB().getHostname(), config.getInfluxDB().getUsername(), config.getInfluxDB().getPassword());
        influxDB.setDatabase(config.getInfluxDB().getDatabase());

        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new CollectorManager(influxDB).registerCollector(new StatusCollector(config.getCollectors().getMinecraft())), 0, config.getCollectors().getPeriod(), TimeUnit.MILLISECONDS);
    }
}
