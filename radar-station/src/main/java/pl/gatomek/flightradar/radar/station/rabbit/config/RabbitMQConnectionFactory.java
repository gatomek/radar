package pl.gatomek.flightradar.radar.station.rabbit.config;

import com.rabbitmq.client.ConnectionFactory;
import pl.gatomek.flightradar.radar.station.config.RadarProperties;

public class RabbitMQConnectionFactory {

    private final ConnectionFactory connectionFactory;

    public RabbitMQConnectionFactory(RadarProperties props) {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(props.get("rabbit.host"));
        factory.setPort(Integer.parseInt(props.get("rabbit.port")));
        factory.setUsername(props.get("rabbit.username"));
        factory.setPassword(props.get("rabbit.password"));
        factory.setAutomaticRecoveryEnabled(true);

        this.connectionFactory = factory;
    }

    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }
}
