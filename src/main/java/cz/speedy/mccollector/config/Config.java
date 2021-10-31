package cz.speedy.mccollector.config;

import com.google.gson.annotations.SerializedName;
import cz.speedy.mccollector.McCollector;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Config {

    public static Config loadConfig(String fileName) throws IOException {
        File file = new File(fileName);
        Config config = new Config();
        if(!file.exists()) {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(file);
                McCollector.GSON.toJson(config, fileWriter);
            } finally {
                if(fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
            }
            System.exit(0);
        } else {
            try (FileReader fileReader = new FileReader(file)) {
                config = McCollector.GSON.fromJson(fileReader, Config.class);
            }
        }
        return config;
    }

    @SerializedName("influxdb")
    private InfluxDB influxDB = new InfluxDB();

    @SerializedName("collectors")
    private Collectors collectors = new Collectors();

    public InfluxDB getInfluxDB() {
        return influxDB;
    }

    public Collectors getCollectors() {
        return collectors;
    }

    public static class InfluxDB {

        @SerializedName("hostname")
        private String hostname = "localhost";

        @SerializedName("username")
        private String username = "mccollector";

        @SerializedName("password")
        private String password = "";

        @SerializedName("database")
        private String database = "mccollector";

        public String getHostname() {
            return hostname;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getDatabase() {
            return database;
        }
    }

    public static class Collectors {

        @SerializedName("period")
        private long period = 1000;

        @SerializedName("minecraft")
        private Minecraft minecraft = new Minecraft();

        public long getPeriod() {
            return period;
        }

        public Minecraft getMinecraft() {
            return minecraft;
        }

        public static class Minecraft {

            @SerializedName("servers")
            private Set<Server> servers = new HashSet<Server>(List.of(new Server()));

            @SerializedName("socket_timeout")
            private int socketTimeout = 10000;

            public Set<Server> getServers() {
                return servers;
            }

            public int getSocketTimeout() {
                return socketTimeout;
            }

            public static class Server {

                @SerializedName("name")
                private String name = "Localhost";

                @SerializedName("address")
                private String address = "localhost";

                @SerializedName("port")
                private int port = 25565;

                public String getName() {
                    return name;
                }

                public String getAddress() {
                    return address;
                }

                public int getPort() {
                    return port;
                }
            }
        }
    }
}
