/**
 * Finance : échéances de loyer (étape 4.1), puis paiements, imputations, quittances (lots suivants).
 * Dépend de {@code bail::api} en lecture ; ne dépend jamais de {@code paiementenligne}.
 */
@ApplicationModule(displayName = "Finance", allowedDependencies = {"shared", "bail :: api", "tiers :: api", "securite", "patrimoine :: api", "stockage"})
package com.immogest.app.finance;

import org.springframework.modulith.ApplicationModule;
