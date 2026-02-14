package pl.gatomek.flightradar.radar.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.gatomek.flightradar.radar.service.QueryServicePort;


@RequiredArgsConstructor
@Component
public class QueryScheduler {
    private final QueryServicePort queryServicePort;

    @Scheduled(cron = "*/15 * * * * *")
    public void query() {
        queryServicePort.query();
    }
}
