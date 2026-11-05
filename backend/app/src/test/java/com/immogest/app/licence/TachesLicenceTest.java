package com.immogest.app.licence;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.kagkarlsson.scheduler.task.helper.RecurringTask;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TachesLicenceTest extends BaseLicenceTest {

    @Autowired
    List<RecurringTask<?>> taches;

    @Test
    void leControleQuotidienEstDeclareAuPlanificateur() {
        assertThat(taches).extracting(RecurringTask::getName).contains("licence-controle-quotidien");
    }

    @Test
    void lesTablesDuPlanificateurExistent() {
        // Tâches persistantes en base (pas de Redis) : la migration technique est appliquée
        assertThat(jdbc.queryForObject("SELECT count(*) FROM scheduled_tasks", Integer.class)).isNotNegative();
    }
}
