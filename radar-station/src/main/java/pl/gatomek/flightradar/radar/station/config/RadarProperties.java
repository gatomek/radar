package pl.gatomek.flightradar.radar.station.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class RadarProperties {
    private final Properties props = new Properties();
    private final Map<String, String> values = HashMap.newHashMap(7);

    public void load(InputStream is) throws IOException {
        props.load(is);
    }

    public String getRabbitHost() {
        return get("rabbit.host");
    }

    public int getRabbitPort() {
        String port = get("rabbit.port");
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("RabbitMQ configuration error: 'rabbit.port' must be a valid integer, but was: '" + port + "'", e);
        }
    }

    public String getRabbitUsername() {
        return get("rabbit.username");
    }

    public String getRabbitPassword() {
        return get("rabbit.password");
    }

    public String getRadarLat() {
        return get("radar.lat");
    }

    public String getRadarLon() {
        return get("radar.lon");
    }

    public String getRadarRange() {
        return get("radar.range");
    }

    private String get(String name) throws UnsupportedOperationException {
        String s = values.get(name);
        if (s != null) return s;

        String v = getCore(name);

        if (v == null) {
            throw new IllegalArgumentException("Configuration error: '" + name + "' property is missing.");
        }

        if (v.trim().isEmpty()) {
            throw new IllegalArgumentException("Configuration error: '" + name + "' property is empty.");
        }

        values.put(name, v);
        return v;
    }

    private String getCore(String name) throws UnsupportedOperationException {
        String key = name.replace(".", "_");
        String ev = System.getenv(key);
        if (ev != null) {
            return ev;
        }

        return props.getProperty(name);
    }
}
