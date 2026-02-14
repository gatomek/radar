package pl.gatomek.flightradar.radar.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.gatomek.flightradar.radar.client.RadarClient;
import pl.gatomek.flightradar.radar.model.AircraftFile;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
class QueryService implements QueryServicePort {

    private final RadarClient radarClient;
    private static final double LAT = 52.162;
    private static final double LON = 20.960;
    private static final int RANGE = 250;

    @Override
    public void query() {
        try {
            ResponseEntity<AircraftFile> radarData = radarClient.getRadarData(LAT, LON, RANGE);
            log.info("status:{} | lat:{} | lon:{} | range:{} | n:{}",
                    radarData.getStatusCode(),
                    LAT, LON, RANGE,
                    Optional.ofNullable(radarData.getBody()).map(AircraftFile::getTotal).orElse(-1));
        } catch (FeignException ex) {
            log.error("HTTP Status: {}\nMessage: {} ", ex.status(), ex.getMessage());
        }
    }
}
