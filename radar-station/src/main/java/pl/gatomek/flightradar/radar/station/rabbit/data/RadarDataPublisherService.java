package pl.gatomek.flightradar.radar.station.rabbit.data;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.gatomek.flightradar.radar.station.rabbit.config.RabbitMQConnectionFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class RadarDataPublisherService {
    private static final String QUEUE_NAME = "RADAR_DATA";

    private final RabbitMQConnectionFactory connectionFactory;

    private Connection connection;
    private Channel channel;

    public RadarDataPublisherService(RabbitMQConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void open() throws IOException, TimeoutException {
        ExecutorService es = Executors.newFixedThreadPool(1);
        connection = connectionFactory.getConnectionFactory().newConnection(es);
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
    }

    public void close() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }

    public void publishRadarData(String radarData) throws IOException {
        channel.basicPublish("", QUEUE_NAME, null, radarData.getBytes());
    }
}
