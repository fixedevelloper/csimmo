/**
 * Relances des loyers impayés (rappel J-5, relances J+3, J+10, J+30 paramétrables), liste des arriérés, mise en demeure PDF.
 * Les messages passent par {@code notification} (premier canal disponible : WhatsApp → push → SMS).
 */
@ApplicationModule(displayName = "Relances", allowedDependencies = {"shared", "finance", "bail :: api", "tiers :: api", "patrimoine :: api",
        "notification", "stockage"})
package com.immogest.app.relance;

import org.springframework.modulith.ApplicationModule;
