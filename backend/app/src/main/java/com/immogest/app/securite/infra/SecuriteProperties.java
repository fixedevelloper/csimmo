package com.immogest.app.securite.infra;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Configuration {@code immogest.securite.*}. */
@ConfigurationProperties("immogest.securite")
public record SecuriteProperties(
        String jwtSecret,
        int accesDureeMinutes,
        int refreshDureeJours,
        int otpDureeMinutes,
        int otpIntervalleSecondes,
        int otpTentativesMax,
        int echecsMax,
        int verrouillageMinutes,
        int limiteAuthParMinute,
        int limiteRefreshParMinute,
        /** Nom affiché dans l'application d'authentification (Google Authenticator…). */
        String totpEmetteur) {

    /** Valeurs par défaut des limites de débit (par adresse IP) si elles ne sont pas configurées. */
    public SecuriteProperties {
        limiteAuthParMinute = limiteAuthParMinute <= 0 ? 20 : limiteAuthParMinute;
        limiteRefreshParMinute = limiteRefreshParMinute <= 0 ? 60 : limiteRefreshParMinute;
        totpEmetteur = totpEmetteur == null || totpEmetteur.isBlank() ? "ImmoGest" : totpEmetteur;
    }
}
