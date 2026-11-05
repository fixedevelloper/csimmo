package com.immogest.app.licence;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/** Horloge pilotable pour voyager dans le temps (expiration, grâce, alertes). Les vues par fuseau suivent la même heure. */
public class MutableClock extends Clock {

    // Microsecondes : précision de PostgreSQL (une date relue en base ne doit pas être postérieure à l'instant écrit)
    private final Instant depart = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.MICROS);
    private volatile Instant instant = depart;
    private final ZoneId zone;
    private final MutableClock racine;

    public MutableClock() {
        this.zone = ZoneId.of("UTC");
        this.racine = this;
    }

    private MutableClock(MutableClock racine, ZoneId zone) {
        this.zone = zone;
        this.racine = racine;
    }

    public void avancerJours(long jours) {
        racine.instant = racine.instant.plus(Duration.ofDays(jours));
    }

    public void avancer(Duration duree) {
        racine.instant = racine.instant.plus(duree);
    }

    public void reinitialiser() {
        racine.instant = racine.depart;
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId nouvelleZone) {
        return new MutableClock(racine, nouvelleZone);
    }

    @Override
    public Instant instant() {
        return racine.instant;
    }
}
