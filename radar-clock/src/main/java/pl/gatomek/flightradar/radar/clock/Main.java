package pl.gatomek.flightradar.radar.clock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.*;


public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        int interval = 5;
        if (args.length > 0) {
            String arg0 = args[0];
            interval = Integer.parseInt(arg0);
        }
        LOGGER.info("Interval: {}", interval);

        RabbitService rabbitService = new RabbitService();

        try (ScheduledExecutorService scheduler =
                     Executors.newSingleThreadScheduledExecutor(namedThreadFactory("radar-tick"))) {
            try {
                rabbitService.open();

                Runnable tickTask = () -> {
                    try {
                        rabbitService.sendTick();
                    } catch (Exception ex) {
                        LOGGER.error("Tick task failed", ex);
                    }
                };

                scheduler.scheduleWithFixedDelay(tickTask, interval, interval, TimeUnit.SECONDS);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdownAndAwait(scheduler), "radar-shutdown"));
                awaitForever();
            } finally {
                rabbitService.close();
            }
        } catch (IOException ex) {
            LOGGER.error("IO Exception", ex);
        } catch (TimeoutException ex) {
            LOGGER.error("Timeout Exception", ex);
        }
    }

    private static void shutdownAndAwait(ScheduledExecutorService scheduler) {
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
    }

    private static ThreadFactory namedThreadFactory(String baseName) {
        return r -> {
            Thread t = new Thread(r);
            t.setName(baseName);
            t.setDaemon(false);
            return t;
        };
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
