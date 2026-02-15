package pl.gatomek.flightradar.radar.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.gatomek.flightradar.radar.client.RadarClient;
import pl.gatomek.flightradar.radar.config.RadarConfig;
import pl.gatomek.flightradar.radar.model.AircraftFile;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
class QueryService implements QueryServicePort {
    private final RadarConfig radarConfig;
    private final RadarClient radarClient;

    @Override
    public void query() {
        float lat = radarConfig.getLat();
        float lon = radarConfig.getLon();
        int range = radarConfig.getRange();

        try {
            ResponseEntity<AircraftFile> radarData = radarClient.getRadarData(lat, lon, range);
            log.info("status:{} | lat:{} | lon:{} | range:{} | n:{}",
                    radarData.getStatusCode(),
                    radarConfig.getLat(), radarConfig.getLon(), radarConfig.getRange(),
                    Optional.ofNullable(radarData.getBody()).map(AircraftFile::getTotal).orElse(-1));
        } catch (FeignException ex) {
            log.error("HTTP Status: {}\nMessage: {} ", ex.status(), ex.getMessage(), ex);
        }
    }
}
