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

    public static void main(String[] args) {
        String localization = getLocalization(args);
        LOGGER.info("Localization: {}", localization);

        RadarProperties props = loadProps(localization);

        String url = resolveUrl(props);
        LOGGER.info("URL: {}", url);

        OkHttpClient httpClient = makeHttpClient();

        AircraftLogClientService logClientService = new AircraftLogClientService(httpClient, url);
        RabbitMQConnectionFactory rabbitMQConnectionFactory = new RabbitMQConnectionFactory(props);
        AircraftLogPublisherService logPublisherService = new AircraftLogPublisherService(rabbitMQConnectionFactory);

        try (ExecutorService es = Executors.newSingleThreadExecutor()) {
            Runnable task = () ->
                    CompletableFuture
                            .supplyAsync(logClientService::getAircraftLogs, es)
                            .thenApplyAsync(logs -> Optional.ofNullable(logs).orElseThrow(
                                    () -> new RuntimeException("Aircraft logs are null or could not be retrieved")), es)
                            .thenAcceptAsync(logPublisherService::publishAircraftLog, es)
                            .exceptionallyAsync(ex -> {
                                        LOGGER.error("Main", ex);
                                        return null;
                                    }, es
                            );

            RadarClockClientService clockClientService = new RadarClockClientService(rabbitMQConnectionFactory, task);

            boolean logPublisherOpened = false;
            boolean clockClientOpened = false;

            try {
                logPublisherService.open();
                logPublisherOpened = true;

                clockClientService.open();
                clockClientOpened = true;

                awaitForever();
            } catch (IOException ex) {
                LOGGER.error("IO Exception", ex);
            } catch (TimeoutException ex) {
                LOGGER.error("Timeout Exception", ex);
            } finally {
                if (clockClientOpened) {
                    try {
                        clockClientService.close();
                    } catch (Exception ex) {
                        LOGGER.warn("Failed to close RadarClockClientService", ex);
                    }
                }

                if (logPublisherOpened) {
                    try {
                        logPublisherService.close();
                    } catch (Exception ex) {
                        LOGGER.warn("Failed to close AircraftLogPublisherService", ex);
                    }
                }

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

    private static String resolveUrl(RadarProperties props) {
        String lat = props.getRadarLat();
        String lon = props.getRadarLon();
        String range = props.getRadarRange();

        return MessageFormat.format(URL_PATTERN, lat, lon, range);
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

    private static RadarProperties loadProps(String localization) {
        String propFileName = "application-" + localization + ".properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream in = loader.getResourceAsStream(propFileName);
        if (in == null) {
            throw new IllegalStateException("Application property file '" + propFileName + "' not found on the classpath");
        }

        RadarProperties radarProps = new RadarProperties();
        try (InputStream is = in) {
            radarProps.load(is);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load application property file '" + propFileName + "'", e);
        }

        return radarProps;
    }

    private static String getLocalization(String[] args) {
        if (args.length > 0) {
            return args[0];
        }

        throw new UnsupportedOperationException("Localization is not given");
    }
}
