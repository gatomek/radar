package pl.gatomek.flightradar.radar.clock;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

public class RabbitService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitService.class);
    private Connection connection;
    private Channel channel;

    public void open() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("113-30-190-16.cloud-xip.com");
        factory.setPort(5673);
        factory.setUsername("gatomi");
        factory.setPassword("Logan@2127.rmq");

        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare("RADAR_CLOCK", "fanout", true);
    }

    public void sendTick() throws IOException {
        String message = String.valueOf(Instant.now().toEpochMilli() / 1000);
        channel.basicPublish("RADAR_CLOCK", "", null, message.getBytes());
        LOGGER.info("Sent '{}'", message);
    }

    public void close() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }
}
