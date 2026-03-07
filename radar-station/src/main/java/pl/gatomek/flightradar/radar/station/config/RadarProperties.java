package pl.gatomek.flightradar.radar.station.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class RadarProperties extends Properties {

    private final Map<String, String> values = new HashMap<>();

    @Override
    public String getProperty(String name) throws UnsupportedOperationException {
        String s = values.get(name);
        if (s != null)
            return s;

        String key = name.replace(".", "_");
        String ev = System.getenv(key);
        if (ev != null) {
            values.put(name, ev);
            return ev;
        }

        String pv = super.getProperty(name);
        if (pv != null) {
            values.put(name, pv);
            return pv;
        }

        throw new UnsupportedOperationException("Property " + name + " was not found");
    }
}
