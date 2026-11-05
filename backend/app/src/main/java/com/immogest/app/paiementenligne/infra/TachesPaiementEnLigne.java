package com.immogest.app.paiementenligne.infra;

import com.github.kagkarlsson.scheduler.task.helper.RecurringTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import com.github.kagkarlsson.scheduler.task.schedule.Schedules;
import com.immogest.app.paiementenligne.PaiementEnLigneService;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Filet de sécurité : un webhook perdu ou en retard ne laisse jamais un paiement non confirmé plus de quelques minutes. */
@Configuration(proxyBeanMethods = false)
class TachesPaiementEnLigne {

    @Bean
    RecurringTask<Void> verificationPaiementsEnLigne(PaiementEnLigneService service) {
        return Tasks.recurring("paiement-en-ligne-verification", Schedules.fixedDelay(Duration.ofMinutes(2))).execute((i, c) -> service.verifierEnAttente());
    }
}
