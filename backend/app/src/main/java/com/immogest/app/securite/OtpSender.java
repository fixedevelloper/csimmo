package com.immogest.app.securite;

/** Envoie un code à usage unique par SMS. Implémentation console en dev ; SMS réel au lot 5.2. */
public interface OtpSender {

    void envoyer(String telephone, String code);
}
