/**
 * Utilisateurs, rôles, authentification (JWT + refresh, OTP, 2FA) et contrôle d'accès.
 */
@ApplicationModule(displayName = "Sécurité", allowedDependencies = {"shared", "stockage"})
package com.immogest.app.securite;

import org.springframework.modulith.ApplicationModule;
