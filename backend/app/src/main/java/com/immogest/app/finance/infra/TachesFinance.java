package com.immogest.app.finance.infra;

import com.github.kagkarlsson.scheduler.task.helper.RecurringTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import com.github.kagkarlsson.scheduler.task.schedule.Schedules;
import com.immogest.app.finance.EcheanceService;
import java.time.LocalTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Chaque nuit (après la fin des baux échus) : échéances, avance, statuts DUE / EN_RETARD. */
@Configuration(proxyBeanMethods = false)
class TachesFinance {

    @Bean
    RecurringTask<Void> echeancesQuotidiennes(EcheanceService echeances) {
        return Tasks.recurring("finance-echeances-quotidien", Schedules.daily(LocalTime.of(3, 0))).execute((i, c) -> echeances.executer());
    }
}
