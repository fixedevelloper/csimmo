package com.immogest.app.notification.infra;

import com.immogest.app.finance.EvenementsPaiement.DocumentEmis;
import com.immogest.app.finance.EvenementsPaiement.PaiementEnregistre;
import com.immogest.app.finance.ModePaiement;
import com.immogest.app.notification.NotificationService;
import com.immogest.app.notification.TypeMessage;
import com.immogest.app.shared.Montants;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/** Traduit les événements de finance en messages aux locataires. */
@Component
class FinanceListener {

    private final NotificationService notifications;

    FinanceListener(NotificationService notifications) {
        this.notifications = notifications;
    }

    /** Tout encaissement en espèces donne un reçu SMS au locataire (risque « espèces non tracées » de l'analyse). */
    @ApplicationModuleListener
    void surPaiement(PaiementEnregistre e) {
        if (e.mode() == ModePaiement.ESPECES) {
            notifications.notifierLocataire(TypeMessage.RECU_ESPECES, e.locataireId(), e.paiementId(),
                    Map.of("montant", sms(e.montant()), "date", Montants.date(LocalDate.now(ZoneId.of("Africa/Douala")))));
        }
    }

    @ApplicationModuleListener
    void surDocument(DocumentEmis e) {
        TypeMessage type = "QUITTANCE".equals(e.type()) ? TypeMessage.QUITTANCE_EMISE : TypeMessage.RECU_EMIS;
        notifications.notifierLocataire(type, e.locataireId(), e.documentId(), Map.of("numero", e.numero(), "montant", sms(e.montant())));
    }

    private static String sms(long montant) {
        return Montants.fcfa(montant).replace(' ', ' ');
    }
}
