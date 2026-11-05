package com.immogest.app.securite.infra;

import com.immogest.app.securite.OtpSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Développement : affiche le code dans les logs. Jamais actif en production. */
@Component
@Profile("!prod")
class ConsoleOtpSender implements OtpSender {

    private static final Logger LOG = LoggerFactory.getLogger(ConsoleOtpSender.class);

    @Override
    public void envoyer(String telephone, String code) {
        LOG.info("[OTP console] code {} pour {}", code, telephone);
    }
}
