package pl.gatomek.flightradar.radar.clock.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.gatomek.flightradar.radar.clock.config.ClockProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

public class RabbitService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitService.class);
    private final ClockProperties clockProperties;
    private Connection connection;
    private Channel channel;

    public RabbitService(ClockProperties clockProperties) {
        this.clockProperties = clockProperties;
    }

    public void open() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(clockProperties.get("rabbit.host"));
        factory.setPort(Integer.parseInt(clockProperties.get("rabbit.port")));
        factory.setUsername(clockProperties.get("rabbit.username"));
        factory.setPassword(clockProperties.get("rabbit.password"));

        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare("RADAR_CLOCK", "fanout", true);
    }

    public void sendTick() throws IOException {
        String message = String.valueOf(Instant.now().toEpochMilli() / 1000);
        channel.basicPublish("RADAR_CLOCK", "", null, message.getBytes(StandardCharsets.UTF_8));
        LOGGER.info("Sent '{}'", message);
    }

    public void close() throws IOException, TimeoutException {
        if (channel != null) {
            channel.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
}
