package pl.gatomek.flightradar.radar.station;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.gatomek.flightradar.radar.station.rabbit.clock.RadarClockSubscriberService;
import pl.gatomek.flightradar.radar.station.rabbit.config.RabbitMQConnectionFactory;
import pl.gatomek.flightradar.radar.station.rabbit.data.RadarDataPublisherService;
import pl.gatomek.flightradar.radar.station.rest.RestService;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String URL = "https://api.airplanes.live/v2/point/52.162/20.960/250";

    public static void main(String[] args) throws IOException, TimeoutException {

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();

        RabbitMQConnectionFactory rabbitMQConnectionFactory = new RabbitMQConnectionFactory();

        RestService restService = new RestService(httpClient, URL);

        RadarDataPublisherService radarDataPublisherService =
                new RadarDataPublisherService(rabbitMQConnectionFactory);

        RadarClockSubscriberService radarClockSubscriberService =
                new RadarClockSubscriberService(restService, rabbitMQConnectionFactory, radarDataPublisherService);

        try {
            radarClockSubscriberService.open();
            radarDataPublisherService.open();
            awaitForever();
        } catch (IOException ex) {
            LOGGER.error("IO Exception", ex);
        } catch (TimeoutException ex) {
            LOGGER.error("Timeout Exception", ex);
        } finally {
            radarClockSubscriberService.close();
            radarDataPublisherService.close();
        }
    }

    private static void awaitForever() {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.info("Main thread interrupted, exiting.");
        }
    }
}
