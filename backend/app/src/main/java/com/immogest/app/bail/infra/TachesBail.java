package com.immogest.app.bail.infra;

import com.github.kagkarlsson.scheduler.task.helper.RecurringTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import com.github.kagkarlsson.scheduler.task.schedule.Schedules;
import com.immogest.app.bail.BailService;
import java.time.LocalTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Chaque nuit : termine les baux échus (persistant en base : rattrapé si l'application était arrêtée). */
@Configuration(proxyBeanMethods = false)
class TachesBail {

    static final String NOM = "bail-statuts-quotidien";

    @Bean
    RecurringTask<Void> statutsDesBaux(BailService baux) {
        return Tasks.recurring(NOM, Schedules.daily(LocalTime.of(2, 30))).execute((instance, contexte) -> baux.mettreAJourStatuts());
    }
}
