package pl.gatomek.flightradar.radar.poc.radarcollector.adapter.in.rabbit;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableRabbit
@Configuration
@EnableConfigurationProperties(RabbitPropertiesConfiguration.class)
public class RabbitConfig {
}
