package com.immogest.app.licence.infra;

import com.github.kagkarlsson.scheduler.task.helper.RecurringTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import com.github.kagkarlsson.scheduler.task.schedule.Schedules;
import com.immogest.app.licence.LicenceService;
import java.time.LocalTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Contrôle quotidien de la licence, persistant en base (db-scheduler) : rattrapé si l'application était arrêtée. */
@Configuration(proxyBeanMethods = false)
class TachesLicence {

    static final String NOM = "licence-controle-quotidien";

    @Bean
    RecurringTask<Void> controleQuotidienLicence(LicenceService licences) {
        return Tasks.recurring(NOM, Schedules.daily(LocalTime.of(3, 30)))
                .execute((instance, contexte) -> licences.controleQuotidien());
    }
}
