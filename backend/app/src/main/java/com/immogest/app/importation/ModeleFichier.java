package com.immogest.app.importation;

/** Accès public (pour le contrôleur, package {@code web}) aux modèles téléchargeables. */
public final class ModeleFichier {

    private ModeleFichier() {
    }

    public static byte[] xlsx() {
        return ModeleImport.xlsx();
    }

    public static byte[] csv(TypeImport type) {
        return ModeleImport.csv(type);
    }
}
