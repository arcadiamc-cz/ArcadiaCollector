package cz.speedy.mccollector.collector.minecraft;

import cz.speedy.mccollector.collector.Collector;
import cz.speedy.mccollector.config.Config;
import cz.speedy.mccollector.utils.MinecraftUtil;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;

import java.util.concurrent.TimeUnit;

public class StatusCollector implements Collector {

    private final Config.Collectors.Minecraft minecraftConfig;

    public StatusCollector(Config.Collectors.Minecraft minecraftConfig) {
        this.minecraftConfig = minecraftConfig;
    }

    @Override
    public void collectAsync(InfluxDB influxDB, long millis) {
        for (Config.Collectors.Minecraft.Server server : minecraftConfig.getServers()) {
            MinecraftUtil.getServerInfo(server.getAddress(), server.getPort(), minecraftConfig.getSocketTimeout()).thenAccept(minecraftStatus -> {
                influxDB.write(Point.measurement("players").tag("name", server.getName()).addField("value", minecraftStatus.getPlayers().getOnline()).time(millis, TimeUnit.MILLISECONDS).build());
            });
        }
    }
}
