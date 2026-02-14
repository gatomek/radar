package pl.gatomek.flightradar.radar.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.gatomek.flightradar.radar.model.AircraftFile;

@FeignClient(name = "radar", url = "https://api.airplanes.live")
public interface RadarClient {

    @GetMapping("/v2/point/{lat}/{lon}/{range}")
    ResponseEntity<AircraftFile> getRadarData(@PathVariable double lat, @PathVariable double lon, @PathVariable int range);
}
