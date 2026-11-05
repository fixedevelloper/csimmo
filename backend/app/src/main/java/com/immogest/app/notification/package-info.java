/**
 * Notifications : file d'envoi persistante avec réessais, canaux SMS / WhatsApp / e-mail / push, journal des envois.
 * Réagit aux événements de {@code finance} (jamais l'inverse). Voir docs/adr/0003-notifications.md.
 */
@ApplicationModule(displayName = "Notifications", allowedDependencies = {"shared", "finance", "tiers :: api", "stockage", "securite"})
package com.immogest.app.notification;

import org.springframework.modulith.ApplicationModule;
