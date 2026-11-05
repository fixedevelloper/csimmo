package com.immogest.app.patrimoine.api;

import java.util.UUID;

/** @param proprietaireTiersId propriétaire tiers mandant ; {@code null} pour un immeuble géré en direct */
public record ImmeubleResume(UUID id, String nom, boolean archive, UUID proprietaireTiersId) {
}
