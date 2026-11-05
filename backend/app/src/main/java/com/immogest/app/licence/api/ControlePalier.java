package com.immogest.app.licence.api;

/** Contrôle du palier de lots de la licence (règle de licence n°5). */
public interface ControlePalier {

    /**
     * À appeler avant de créer un lot.
     *
     * @param nombreDeLotsActuel nombre de lots existants
     * @throws com.immogest.app.shared.ErreurMetier {@code LICENCE_PALIER_ATTEINT} si un lot de plus dépasserait le
     *                                              palier, {@code LICENCE_EXPIREE} si la licence n'est pas valide
     */
    void verifierCreationLot(long nombreDeLotsActuel);

    /** Vrai si la licence est valide : sinon l'application est en lecture seule (les tâches de fond n'écrivent plus rien). */
    boolean ecrituresAutorisees();

    /** Palier de la licence courante (0 si aucune licence valide). */
    int palierLots();
}
