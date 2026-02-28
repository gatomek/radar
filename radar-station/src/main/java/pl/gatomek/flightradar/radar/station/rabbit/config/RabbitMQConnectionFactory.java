package pl.gatomek.flightradar.radar.station.rabbit.config;

import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQConnectionFactory {

    private final ConnectionFactory connectionFactory;

    public RabbitMQConnectionFactory() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("113-30-190-16.cloud-xip.com");
        connectionFactory.setPort(5673);
        connectionFactory.setUsername("gatomi");
        connectionFactory.setPassword("Logan@2127.rmq");
        connectionFactory.setAutomaticRecoveryEnabled(true);

        this.connectionFactory = connectionFactory;
    }

    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }
}
