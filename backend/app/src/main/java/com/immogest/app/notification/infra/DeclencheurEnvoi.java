package com.immogest.app.notification.infra;

import com.immogest.app.notification.NotificationService;
import com.immogest.app.notification.NotificationService.FileAlimentee;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** Envoie sans attendre la tâche périodique, mais seulement une fois les messages validés en base. */
@Component
class DeclencheurEnvoi {

    private final NotificationService notifications;

    DeclencheurEnvoi(NotificationService notifications) {
        this.notifications = notifications;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void apresCommit(FileAlimentee e) {
        notifications.traiterFile();
    }
}
