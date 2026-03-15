package pl.gatomek.flightradar.radar.poc.radarcollector.adapter.out.database;

import org.springframework.stereotype.Component;
import pl.gatomek.flightradar.radar.poc.radarcollector.application.domain.model.AircraftLog;
import pl.gatomek.flightradar.radar.poc.radarcollector.application.domain.model.AircraftNotification;
import pl.gatomek.flightradar.radar.poc.radarcollector.application.port.out.CleanUpDataPort;
import pl.gatomek.flightradar.radar.poc.radarcollector.application.port.out.LogQueryPort;
import pl.gatomek.flightradar.radar.poc.radarcollector.application.port.out.NotificationCommandPort;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
class DataBase implements NotificationCommandPort, LogQueryPort, CleanUpDataPort {

    private final Map<String, AircraftLog> cache = HashMap.newHashMap(500);
    private final Lock readLock;
    private final Lock writeLock;

    public DataBase() {
        final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        readLock = rwLock.readLock();
        writeLock = rwLock.writeLock();
    }

    @Override
    public AircraftNotification getLogs() {
        AircraftNotification an = new AircraftNotification();

        List<AircraftLog> logs;

        readLock.lock();
        try {
            logs = cache.values().stream().toList();
        } finally {
            readLock.unlock();
        }

        an.setAircraftLogs(logs);
        an.setTimestamp(Instant.now().toEpochMilli());
        an.setTotal(logs.size());

        return an;
    }

    @Override
    public void cleanUp() {
        Instant filterTimestamp = Instant.now().minus(1, ChronoUnit.MINUTES);
        writeLock.lock();
        try {
            cache.entrySet().removeIf(p -> p.getValue().getTimestamp().isBefore(filterTimestamp));
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void notify(AircraftNotification an) {
        Instant notificationTimestamp = Instant.ofEpochMilli(an.getTimestamp());

        for (AircraftLog newAircraftLog : an.getAircraftLogs()) {
            String icao = newAircraftLog.getIcao();
            newAircraftLog.setTimestamp(notificationTimestamp);

            writeLock.lock();
            try {

                AircraftLog oldAircraftLog = cache.get(icao);
                if (oldAircraftLog == null) {
                    cache.put(icao, newAircraftLog);
                    continue;
                }

                Instant oldTimestamp = oldAircraftLog.getTimestamp();
                Instant newTimestamp = newAircraftLog.getTimestamp();

                if (newTimestamp.isAfter(oldTimestamp)) {
                    cache.put(icao, newAircraftLog);
                }
            } finally {
                writeLock.unlock();
            }
        }
    }
}
