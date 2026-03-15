package pl.gatomek.flightradar.radar.poc.radarcollector.application.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.gatomek.flightradar.radar.poc.radarcollector.application.domain.model.AircraftNotification;
import pl.gatomek.flightradar.radar.poc.radarcollector.application.port.in.NotificationPort;
import pl.gatomek.flightradar.radar.poc.radarcollector.application.port.out.NotificationCommandPort;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationService implements NotificationPort {
    private final NotificationCommandPort notificationCommandPort;

    @Override
    public void notify(AircraftNotification an) {
        notificationCommandPort.notify(an);
    }
}
