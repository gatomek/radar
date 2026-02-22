package pl.gatomek.flightradar.radar.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "radar")
@Data
public class RadarConfig {
    Float lat;
    Float lon;
    Integer range;
}
