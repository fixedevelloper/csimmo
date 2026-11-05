package com.immogest.app.audit;

import static org.assertj.core.api.Assertions.assertThat;

import com.immogest.app.TestcontainersConfiguration;
import com.immogest.app.audit.domain.AuditLog;
import com.immogest.app.audit.infra.AuditLogRepository;
import com.immogest.app.shared.EvenementMetier;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ApplicationModuleTest
@Import(TestcontainersConfiguration.class)
class AuditModuleTest {

    record PaiementTeste(UUID id, long montant) implements EvenementMetier {
        @Override
        public String action() {
            return "TEST.PAIEMENT_ENREGISTRE";
        }

        @Override
        public String entite() {
            return "paiement";
        }

        @Override
        public UUID entiteId() {
            return id;
        }

        @Override
        public Object apres() {
            return Map.of("montant", montant);
        }

        @Override
        public String auteur() {
            return "gestionnaire-1";
        }
    }

    @Autowired
    AuditLogRepository repository;

    @Autowired
    JdbcTemplate jdbc;

    @Test
    void journaliseUnEvenementMetier(Scenario scenario) {
        UUID id = UUID.randomUUID();

        scenario.publish(new PaiementTeste(id, 150_000))
                .andWaitForStateChange(() -> repository.findByEntiteIdOrderByDateAsc(id), l -> !l.isEmpty())
                .andVerify(lignes -> {
                    assertThat(lignes).hasSize(1);
                    AuditLog ligne = lignes.get(0);
                    assertThat(ligne.getAction()).isEqualTo("TEST.PAIEMENT_ENREGISTRE");
                    assertThat(ligne.getAuteur()).isEqualTo("gestionnaire-1");
                    assertThat(ligne.getAvant()).isNull();
                    assertThat(ligne.getApres()).containsEntry("montant", 150_000);
                });
    }

    @Test
    void lesLignesDAuditNePeuventPasEtreSupprimees(Scenario scenario) {
        UUID id = UUID.randomUUID();
        scenario.publish(new PaiementTeste(id, 1))
                .andWaitForStateChange(() -> repository.findByEntiteIdOrderByDateAsc(id), l -> !l.isEmpty())
                .andVerify(l -> assertThatThrownBy(() -> jdbc.update("DELETE FROM audit_log WHERE entite_id = ?", id))
                        .isInstanceOf(DataAccessException.class)
                        .hasMessageContaining("ajout seul"));
    }
}
