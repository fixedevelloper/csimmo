package com.immogest.app.finance.infra;

import com.immogest.app.finance.DocumentService;
import com.immogest.app.finance.EvenementsPaiement.EcheancePayee;
import com.immogest.app.finance.EvenementsPaiement.RecuAEmettre;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/** Émission asynchrone, après commit : un PDF en échec ne bloque jamais un encaissement (l'événement reste dans l'outbox). */
@Component
class DocumentsListener {

    private final DocumentService documents;

    DocumentsListener(DocumentService documents) {
        this.documents = documents;
    }

    @ApplicationModuleListener
    void surEcheancePayee(EcheancePayee evenement) {
        documents.emettreQuittance(evenement.echeanceId());
    }

    @ApplicationModuleListener
    void surRecuAEmettre(RecuAEmettre evenement) {
        documents.emettreRecu(evenement.paiementId());
    }
}
