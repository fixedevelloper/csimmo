package com.immogest.app.notification.infra;

import com.immogest.app.notification.NotificationService;
import com.immogest.app.securite.OtpSender;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * Envoi des codes OTP par le canal SMS du client quand il est actif ; sinon repli sur l'implémentation de {@code securite}
 * (console en développement, refus explicite en production). Le code n'est jamais journalisé ni mis en file.
 */
@Component
@jakarta.annotation.Priority(10) // choisi parmi les implémentations de securite ; un @Primary explicite (tests) garde la priorité
class OtpSenderSms implements OtpSender {

    private final NotificationService notifications;
    private final ObjectProvider<OtpSender> autres;

    OtpSenderSms(NotificationService notifications, ObjectProvider<OtpSender> autres) {
        this.notifications = notifications;
        this.autres = autres;
    }

    @Override
    public void envoyer(String telephone, String code) {
        if (notifications.smsDisponible()) {
            notifications.envoyerSmsDirect(telephone, notifications.texteOtp(code));
            return;
        }
        autres.stream().filter(o -> o != this).findFirst().orElseThrow().envoyer(telephone, code);
    }
}
