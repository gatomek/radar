package pl.gatomek.flightradar.radar.station.rabbit.clock;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.gatomek.flightradar.radar.station.rabbit.config.RabbitMQConnectionFactory;
import pl.gatomek.flightradar.radar.station.rabbit.data.RadarDataPublisherService;
import pl.gatomek.flightradar.radar.station.rest.RestService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class RadarClockSubscriberService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RadarClockSubscriberService.class);
    private static final String EXCHANGE_NAME = "RADAR_CLOCK";
    private final RestService restService;
    private final RabbitMQConnectionFactory connectionFactory;
    private final RadarDataPublisherService radarDataPublisherService;
    private Connection connection;
    private Channel channel;

    public RadarClockSubscriberService(RestService restService,
                                       RabbitMQConnectionFactory connectionFactory,
                                       RadarDataPublisherService radarDataPublisherService) {
        this.restService = restService;
        this.connectionFactory = connectionFactory;
        this.radarDataPublisherService = radarDataPublisherService;
    }

    public void open() throws IOException, TimeoutException {
        ExecutorService es = Executors.newFixedThreadPool(1);
        connection = connectionFactory.getConnectionFactory().newConnection(es);
        channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout", true);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            String logs = restService.getAircraftLogs();
            LOGGER.info("Received: {} | {}", message, logs.length());
            radarDataPublisherService.publishRadarData(logs);
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }

    public void close() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }
}
