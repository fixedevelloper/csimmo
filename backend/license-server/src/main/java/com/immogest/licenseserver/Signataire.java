package com.immogest.licenseserver;

import com.immogest.licensing.Ed25519;
import com.immogest.licensing.Licence;
import com.immogest.licensing.LicenceSignee;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import org.springframework.stereotype.Component;

/** Signe les licences avec la clé privée de l'éditeur, lue depuis un fichier hors dépôt. Jamais journalisée ni exposée. */
@Component
class Signataire {

    private final PrivateKey clePrivee;

    Signataire(Proprietes p) throws IOException {
        if (p.clePriveeChemin() == null || p.clePriveeChemin().isBlank()) {
            throw new IllegalStateException("LICENSE_SERVER_CLE_PRIVEE_CHEMIN est obligatoire (fichier de la clé privée, hors dépôt)");
        }
        this.clePrivee = Ed25519.decoderClePrivee(Files.readString(Path.of(p.clePriveeChemin())));
    }

    String signer(Licence licence) {
        return LicenceSignee.signer(licence, clePrivee).versJson();
    }
}
