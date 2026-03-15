package pl.gatomek.flightradar.radar.poc.radarcollector.application.port.in;

import pl.gatomek.flightradar.radar.poc.radarcollector.application.domain.model.AircraftNotification;

public interface LogPort {
    AircraftNotification getLogs();
}
