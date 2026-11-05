package com.immogest.app.bail.infra;

import java.util.List;

/** Tout ce que la mise en page du bail affiche, déjà formaté (textes en clair : Thymeleaf les échappe). */
public record DonneesBail(String numero, boolean brouillon, String bailleurNom, String bailleurAdresse, List<String> preambule,
        String locataireNom, String locataireProfession, String locataireNaissance, String locataireCni, String locataireAdresse,
        String locataireContact, String immeuble, String adresse, String lot, String dateDebut, String dateFin, String loyer,
        String charges, String periodicite, String echeance, String caution, String avance, String preavis, List<Garant> garants,
        List<String> clauses, String piedDePage) {

    public record Garant(String lien, String nom, String details) {
    }
}
