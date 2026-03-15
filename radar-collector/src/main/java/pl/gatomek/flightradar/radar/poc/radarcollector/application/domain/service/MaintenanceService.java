package pl.gatomek.flightradar.radar.poc.radarcollector.application.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.gatomek.flightradar.radar.poc.radarcollector.application.port.in.MaintenancePort;
import pl.gatomek.flightradar.radar.poc.radarcollector.application.port.out.CleanUpDataPort;

@RequiredArgsConstructor
@Component
class MaintenanceService implements MaintenancePort {

    private final CleanUpDataPort cleanUpDataPort;

    @Override
    public void maintain() {
        cleanUpDataPort.cleanUp();
    }
}
