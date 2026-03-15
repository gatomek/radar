package pl.gatomek.flightradar.radar.poc.radarcollector.application.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.gatomek.flightradar.radar.poc.radarcollector.application.domain.model.AircraftNotification;
import pl.gatomek.flightradar.radar.poc.radarcollector.application.port.in.LogPort;
import pl.gatomek.flightradar.radar.poc.radarcollector.application.port.out.LogQueryPort;

@Slf4j
@RequiredArgsConstructor
@Component
public class LogService implements LogPort {

    private final LogQueryPort logQueryPort;

    @Override
    public AircraftNotification getLogs() {
        return logQueryPort.getLogs();
    }
}
