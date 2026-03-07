package pl.gatomek.flightradar.radar.station.rabbit.config;

import com.rabbitmq.client.ConnectionFactory;

import java.util.Properties;

public class RabbitMQConnectionFactory {

    private final ConnectionFactory connectionFactory;

    public RabbitMQConnectionFactory(Properties props) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(props.getProperty("rabbit.host"));
        factory.setPort(Integer.parseInt(props.getProperty("rabbit.port")));
        factory.setUsername(props.getProperty("rabbit.username"));
        factory.setPassword(props.getProperty("rabbit.password"));
        factory.setAutomaticRecoveryEnabled(true);

        this.connectionFactory = factory;
    }

    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }
}
