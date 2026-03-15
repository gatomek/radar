package pl.gatomek.flightradar.radar.poc.radarcollector.adapter.in.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.gatomek.flightradar.radar.poc.radarcollector.application.port.in.MaintenancePort;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class MaintenanceTask {

    private final MaintenancePort maintenancePort;

    @Scheduled(fixedDelay = 15, timeUnit = TimeUnit.SECONDS)
    public void projectView() {
        maintenancePort.maintain();
    }
}
