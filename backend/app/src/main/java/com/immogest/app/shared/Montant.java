package com.immogest.app.shared;

/** Montant en FCFA entiers (XAF n'a pas de décimales). Jamais de {@code double}. */
public record Montant(long valeur) implements Comparable<Montant> {

    public static final Montant ZERO = new Montant(0);

    public static Montant of(long valeur) {
        return new Montant(valeur);
    }

    public Montant plus(Montant autre) {
        return new Montant(Math.addExact(valeur, autre.valeur));
    }

    public Montant moins(Montant autre) {
        return new Montant(Math.subtractExact(valeur, autre.valeur));
    }

    /** Applique un pourcentage exprimé en points de base (1000 = 10 %), arrondi à l'unité la plus proche. */
    public Montant pourcentage(int pointsDeBase) {
        long produit = Math.multiplyExact(valeur, (long) pointsDeBase);
        long reste = Math.floorMod(produit, 10_000L);
        long quotient = Math.floorDiv(produit, 10_000L);
        return new Montant(reste * 2 >= 10_000L ? quotient + 1 : quotient);
    }

    public Montant min(Montant autre) {
        return valeur <= autre.valeur ? this : autre;
    }

    public boolean estNul() {
        return valeur == 0;
    }

    public boolean estPositif() {
        return valeur > 0;
    }

    @Override
    public int compareTo(Montant autre) {
        return Long.compare(valeur, autre.valeur);
    }
}
