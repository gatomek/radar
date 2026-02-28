package pl.gatomek.flightradar.radar.station.rest;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestService.class);
    private final String url;
    private final OkHttpClient httpClient;

    public RestService(OkHttpClient httpClient, String url) {
        this.url = url;
        this.httpClient = httpClient;
    }

    public String getAircraftLogs() throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            LOGGER.info("HTTP status: {}", response.code());
            return response.body().string();
        }
    }
}
