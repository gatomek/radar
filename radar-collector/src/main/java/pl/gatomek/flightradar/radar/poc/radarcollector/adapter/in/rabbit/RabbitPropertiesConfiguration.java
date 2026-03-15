package pl.gatomek.flightradar.radar.poc.radarcollector.adapter.in.rabbit;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitPropertiesConfiguration {
    @NotEmpty
    private String host;

    @NotNull
    private Integer port;

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;
}
