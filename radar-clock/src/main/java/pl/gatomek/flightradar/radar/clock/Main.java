package pl.gatomek.flightradar.radar.clock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.gatomek.flightradar.radar.clock.config.ClockProperties;
import pl.gatomek.flightradar.radar.clock.rabbit.RabbitService;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.*;


public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final int DEFAULT_INTERVAL_SECONDS = 15;

    public static void main(String[] args) {
        int interval = DEFAULT_INTERVAL_SECONDS;
        if (args.length > 0) {
            String arg0 = args[0];
            try {
                interval = Integer.parseInt(arg0);
                if (interval <= 0) {
                    LOGGER.warn("Invalid interval: {}. Must be positive. Falling back to defaults: {}s", interval, DEFAULT_INTERVAL_SECONDS);
                    interval = DEFAULT_INTERVAL_SECONDS;
                }
            } catch (NumberFormatException e) {
                LOGGER.error("Invalid interval: {}. Falling back to defaults: {}s", arg0, DEFAULT_INTERVAL_SECONDS);
            }
        }
        LOGGER.info("Interval: {}s", interval);

        ClockProperties clockProperties = loadProps();

        RabbitService rabbitService = new RabbitService(clockProperties);
        CountDownLatch shutdownLatch = new CountDownLatch(1);

        Runnable tickTask = () -> {
            try {
                rabbitService.sendTick();
            } catch (Exception ex) {
                LOGGER.error("Tick task failed", ex);
            }
        };

        try (ScheduledExecutorService scheduler =
                     Executors.newSingleThreadScheduledExecutor(namedThreadFactory())) {
            try {
                rabbitService.open();

                scheduler.scheduleWithFixedDelay(tickTask, interval, interval, TimeUnit.SECONDS);
                Runtime.getRuntime().addShutdownHook(new Thread(
                        () -> shutdownAll(scheduler, rabbitService, shutdownLatch), "radar-shutdown"));

                shutdownLatch.await();

            } catch (InterruptedException ie) {
                scheduler.shutdownNow();
                try {
                    rabbitService.close();
                } catch (IOException | TimeoutException e) {
                    LOGGER.error("Error closing RabbitMQ connection", e);
                }
                Thread.currentThread().interrupt();
            }
        } catch (IOException ex) {
            LOGGER.error("IO Exception", ex);
        } catch (TimeoutException ex) {
            LOGGER.error("Timeout Exception", ex);
        }
    }

    private static void shutdownAll(ScheduledExecutorService scheduler, RabbitService rabbitService, CountDownLatch latch) {
        LOGGER.info("Shutting down scheduler...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                LOGGER.warn("Forcing scheduler shutdown...");
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        try {
            rabbitService.close();
        } catch (IOException | TimeoutException e) {
            LOGGER.error("Error closing RabbitMQ connection", e);
        }
        latch.countDown();
    }

    private static ThreadFactory namedThreadFactory() {
        return r -> {
            Thread t = new Thread(r);
            t.setName("radar-tick");
            t.setDaemon(false);
            return t;
        };
    }

    private static ClockProperties loadProps() {
        ClockProperties props = new ClockProperties();

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream in = loader.getResourceAsStream("application.properties");
        if (in == null) {
            throw new IllegalStateException("Application property file 'application.properties' not found on the classpath");
        }

        try (InputStream is = in) {
            props.load(is);
        } catch (IOException e) {
            LOGGER.error("Application property file not found");
        }

        return props;
    }
}
