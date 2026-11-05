package com.immogest.app.relance.infra;

import com.github.kagkarlsson.scheduler.task.helper.RecurringTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import com.github.kagkarlsson.scheduler.task.schedule.Schedules;
import com.immogest.app.relance.RelanceService;
import java.time.LocalTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Chaque matin à 8 h, après le moteur d'échéances de la nuit. */
@Configuration(proxyBeanMethods = false)
class TachesRelance {

    @Bean
    RecurringTask<Void> relancesQuotidiennes(RelanceService relances) {
        return Tasks.recurring("relance-quotidienne", Schedules.daily(LocalTime.of(8, 0))).execute((i, c) -> relances.executer());
    }
}
