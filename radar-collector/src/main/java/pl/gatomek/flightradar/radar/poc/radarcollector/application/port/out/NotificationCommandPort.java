package pl.gatomek.flightradar.radar.poc.radarcollector.application.port.out;

import pl.gatomek.flightradar.radar.poc.radarcollector.application.domain.model.AircraftNotification;

public interface NotificationCommandPort {
    void notify(AircraftNotification an);
}
