package tech.thegamedefault.springboot.kafka.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ThreadPoolProvider {

  private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(20);

  public static ScheduledExecutorService getScheduler() {
    return SCHEDULER;
  }

}
