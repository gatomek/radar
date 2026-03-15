package pl.gatomek.flightradar.radar.poc.radarcollector.adapter.in.rabbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.gatomek.flightradar.radar.poc.radarcollector.application.port.in.NotificationPort;
import pl.gatomek.flightradar.radar.poc.radarcollector.application.domain.model.AircraftNotification;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

@RequiredArgsConstructor
@Slf4j
@Component
public class RadarDataListener {
    private final ObjectMapper objectMapper;
    private final NotificationPort notificationPort;

    private static final String GZIP = "gzip";

    @RabbitListener(queues = "RADAR_DATA")
    public void receiveMessage(Message message) {
        MessageProperties messageProperties = message.getMessageProperties();

        String contentEncoding = messageProperties.getContentEncoding();
        if (GZIP.equals(contentEncoding)) {
            AircraftNotification an = fromGZip(message.getBody());
            notificationPort.notify(an);
        }
    }

    private AircraftNotification fromGZip(byte[] bytes) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {
                    return objectMapper.readValue(inputStreamReader, AircraftNotification.class);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to unzip content", e);
        }
    }
}
