package cz.speedy.mccollector.objects;

import java.util.List;
import java.util.regex.Pattern;

public class MinecraftStatus {

    private Version version;
    private Players players;
    private Description description;
    private String favicon;
    private ModInfo modinfo;

    public Version getVersion() {
        return version;
    }

    public Players getPlayers() {
        return players;
    }

    public Description getDescription() {
        return description;
    }

    public String getFavicon() {
        return favicon;
    }

    public ModInfo getModinfo() {
        return modinfo;
    }

    public static class Version {

        private String name;
        private int protocol;

        public String getName() {
            return name;
        }

        public int getProtocol() {
            return protocol;
        }
    }

    public static class Players {

        private int max;
        private int online;

        public int getMax() {
            return max;
        }

        public int getOnline() {
            return online;
        }
    }

    public static class Description {

        private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)\u00A7[0-9A-FK-ORX]");

        private String text;

        public String getText() {
            return text;
        }

        public String getStrippedText() {
            return STRIP_COLOR_PATTERN.matcher(text).replaceAll("");
        }
    }

    public static class ModInfo {

        private String type;
        private List<String> modList;

        public String getType() {
            return type;
        }

        public List<String> getModList() {
            return modList;
        }
    }
}