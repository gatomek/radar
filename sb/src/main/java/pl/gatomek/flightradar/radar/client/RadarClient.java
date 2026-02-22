package pl.gatomek.flightradar.radar.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "radar", url = "${radar.url}")
public interface RadarClient {

    @GetMapping("/v2/point/{lat}/{lon}/{range}")
    ResponseEntity<String> getRadarData(@PathVariable double lat, @PathVariable double lon, @PathVariable int range);
}
