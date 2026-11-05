package com.immogest.app.licence;

import com.immogest.licensing.LicenceSignee;

/** Accès à la signature d'un fichier .lic dans les tests. */
final class LicenceTestSupportAcces {

    private LicenceTestSupportAcces() {
    }

    static String signature(String fichier) {
        return LicenceSignee.lire(fichier).signature();
    }
}
