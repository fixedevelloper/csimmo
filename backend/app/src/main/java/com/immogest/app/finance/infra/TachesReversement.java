package com.immogest.app.finance.infra;

import com.github.kagkarlsson.scheduler.task.helper.RecurringTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import com.github.kagkarlsson.scheduler.task.schedule.Schedules;
import com.immogest.app.finance.EcheanceService;
import com.immogest.app.finance.ReversementService;
import java.time.LocalTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Chaque matin : (re)calcule le reversement du mois qui vient de s'achever, jusqu'à ce qu'un gestionnaire le valide. */
@Configuration(proxyBeanMethods = false)
class TachesReversement {

    @Bean
    RecurringTask<Void> reversementsMensuels(ReversementService reversements, EcheanceService echeances) {
        return Tasks.recurring("finance-reversements-quotidien", Schedules.daily(LocalTime.of(6, 0)))
                .execute((i, c) -> reversements.calculer(echeances.aujourdhui().withDayOfMonth(1).minusMonths(1)));
    }
}
