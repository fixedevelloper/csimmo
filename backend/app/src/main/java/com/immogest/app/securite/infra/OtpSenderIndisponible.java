package com.immogest.app.securite.infra;

import com.immogest.app.securite.OtpSender;
import com.immogest.app.shared.ErreurMetier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** Production tant qu'aucun fournisseur SMS n'est branché (lot 5.2) : échoue sans jamais journaliser de code. */
@Component
@Profile("prod")
class OtpSenderIndisponible implements OtpSender {

    @Override
    public void envoyer(String telephone, String code) {
        throw new ErreurMetier("OTP_CANAL_INDISPONIBLE", "Aucun canal SMS n'est configuré",
                HttpStatus.SERVICE_UNAVAILABLE);
    }
}
