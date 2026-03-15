package pl.gatomek.flightradar.radar.poc.radarcollector.application.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AircraftLog implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private Instant timestamp;

    @JsonProperty("hex")
    private String icao;

    @JsonProperty("flight")
    private String flight;

    @JsonProperty("r")
    private String registerNumber;

    @JsonProperty("t")
    private String type;

    @JsonProperty("desc")
    private String desc;

    @JsonProperty("lat")
    private BigDecimal latitude;

    @JsonProperty("lon")
    private BigDecimal longitude;

    @JsonProperty("alt_baro")
    private String barometricAltitude;

    @JsonProperty("alt_geom")
    private String geometricAltitude;

    @JsonProperty("mach")
    private BigDecimal mach;

    @JsonProperty("category")
    private String emitterCategory;

    @JsonProperty("emergency")
    private String emergency;

    @JsonProperty("dbFlags")
    private Integer dbFlags;

    @JsonProperty("messages")
    private Integer messages;

    public void setFlight(String flight) {
        this.flight = Optional.ofNullable(flight).map(String::trim).orElse(null);
    }
}
