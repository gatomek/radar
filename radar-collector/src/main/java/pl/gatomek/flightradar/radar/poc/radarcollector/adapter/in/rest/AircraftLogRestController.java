package pl.gatomek.flightradar.radar.poc.radarcollector.adapter.in.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.gatomek.flightradar.radar.poc.radarcollector.application.domain.model.AircraftNotification;
import pl.gatomek.flightradar.radar.poc.radarcollector.application.port.in.LogPort;

@RequiredArgsConstructor
@RestController
public class AircraftLogRestController {
    private final LogPort logPort;

    @GetMapping(value = "/logs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AircraftNotification> getAircraftNotification() {
        AircraftNotification aircraftNotification = logPort.getLogs();
        return ResponseEntity.ok( aircraftNotification);
    }
}
