package pl.gatomek.flightradar.radar.station;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.gatomek.flightradar.radar.station.config.RadarProperties;
import pl.gatomek.flightradar.radar.station.rabbit.clock.RadarClockClientService;
import pl.gatomek.flightradar.radar.station.rabbit.config.RabbitMQConnectionFactory;
import pl.gatomek.flightradar.radar.station.rabbit.log.AircraftLogPublisherService;
import pl.gatomek.flightradar.radar.station.rest.AircraftLogClientService;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String URL_PATTERN = "https://api.airplanes.live/v2/point/{0}/{1}/{2}";

    public static void main(String[] args) throws IOException, TimeoutException {
        String localization = getLocalization(args);
        LOGGER.info("Localization: {}", localization);

        RadarProperties props = loadProperties(localization);
        String lat = props.get("radar.lat");
        String lon = props.get("radar.lon");
        String range = props.get("radar.range");

        OkHttpClient httpClient = makeHttpClient();
        String url = MessageFormat.format(URL_PATTERN, lat, lon, range);

        LOGGER.info("URL: {}", url);

        AircraftLogClientService logClientService = new AircraftLogClientService(httpClient, url);

        RabbitMQConnectionFactory rabbitMQConnectionFactory = new RabbitMQConnectionFactory(props);
        AircraftLogPublisherService logPublisherService = new AircraftLogPublisherService(rabbitMQConnectionFactory);

        try (ExecutorService es = Executors.newSingleThreadExecutor()) {
            Runnable task = () ->
                    CompletableFuture
                            .supplyAsync(logClientService::getAircraftLogs, es)
                            .thenApplyAsync(logs -> Optional.ofNullable(logs).orElseThrow(RuntimeException::new), es)
                            .thenAcceptAsync(logPublisherService::publishAircraftLog, es)
                            .exceptionallyAsync(ex -> {
                                        LOGGER.error("Main", ex);
                                        return null;
                                    }, es
                            );

            RadarClockClientService clockClientService = new RadarClockClientService(rabbitMQConnectionFactory, task);

            try {
                logPublisherService.open();
                clockClientService.open();
                awaitForever();
            } catch (IOException ex) {
                LOGGER.error("IO Exception", ex);
            } catch (TimeoutException ex) {
                LOGGER.error("Timeout Exception", ex);
            } finally {
                clockClientService.close();
                logPublisherService.close();

                es.shutdown();
                try {
                    if (!es.awaitTermination(60, TimeUnit.SECONDS)) {
                        es.shutdownNow();
                    }
                } catch (InterruptedException ie) {
                    es.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private static void awaitForever() {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static OkHttpClient makeHttpClient() {
        return new OkHttpClient.Builder()
                .protocols(List.of(Protocol.HTTP_1_1))
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    private static RadarProperties loadProperties(String localization) {
        RadarProperties props = new RadarProperties();

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream in = loader.getResourceAsStream("application-" + localization + ".properties")) {
            props.load(in);
        } catch (IOException e) {
            LOGGER.error("Application property file not found");
        }

        return props;
    }

    private static String getLocalization(String[] args) {
        if (args.length > 0) {
            return args[0];
        }

        throw new UnsupportedOperationException("Localization is not available");
    }
}
