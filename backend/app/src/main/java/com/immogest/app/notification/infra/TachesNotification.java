package com.immogest.app.notification.infra;

import com.github.kagkarlsson.scheduler.task.helper.RecurringTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import com.github.kagkarlsson.scheduler.task.schedule.Schedules;
import com.immogest.app.notification.NotificationService;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Reprend la file toutes les 30 s : réessais échus, messages restés en attente après un arrêt. */
@Configuration(proxyBeanMethods = false)
class TachesNotification {

    @Bean
    RecurringTask<Void> envoiNotifications(NotificationService notifications) {
        return Tasks.recurring("notification-envoi", Schedules.fixedDelay(Duration.ofSeconds(30))).execute((i, c) -> notifications.traiterFile());
    }
}
