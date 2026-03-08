package pl.gatomek.flightradar.radar.clock.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ClockProperties {
    private final Properties props = new Properties();
    private final Map<String, String> values = new HashMap<>();

    public void load( InputStream is) throws IOException {
        props.load(is);
    }

    public String get(String name) throws UnsupportedOperationException {
        String s = values.get(name);
        if (s != null)
            return s;

        String key = name.replace(".", "_");
        String ev = System.getenv(key);
        if (ev != null) {
            values.put(name, ev);
            return ev;
        }

        String pv = props.getProperty(name);
        if (pv != null) {
            values.put(name, pv);
            return pv;
        }

        throw new UnsupportedOperationException("Property " + name + " was not found");
    }
}
