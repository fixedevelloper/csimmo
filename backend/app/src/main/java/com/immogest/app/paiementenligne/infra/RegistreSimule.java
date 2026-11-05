package com.immogest.app.paiementenligne.infra;

import com.immogest.app.paiementenligne.infra.AgregateurPaiement.AgregateurException;
import com.immogest.app.paiementenligne.infra.AgregateurPaiement.Demande;
import com.immogest.app.paiementenligne.infra.AgregateurPaiement.Etat;
import com.immogest.app.paiementenligne.infra.AgregateurPaiement.EtatDistant;
import com.immogest.app.paiementenligne.infra.AgregateurPaiement.Initiation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Mémoire de l'agrégateur simulé : les tests y « jouent l'opérateur » (le locataire valide, refuse, ou ne répond pas). */
@Component("registreAgregateurSimule")
@ConditionalOnProperty(name = "immogest.paiementenligne.simuler", havingValue = "true")
public class RegistreSimule {

    private static final class Distante {
        Etat etat = Etat.EN_ATTENTE;
        String referenceOperateur;
        Long montant;
    }

    private final Map<String, Distante> transactions = new HashMap<>();
    private final List<Demande> demandes = new ArrayList<>();
    private int echecsInitiation;
    private int echecsVerification;
    private int verifications;

    public synchronized Initiation initier(Demande demande) throws AgregateurException {
        if (echecsInitiation > 0) {
            echecsInitiation--;
            throw new AgregateurException("Agrégateur injoignable (simulé)");
        }
        demandes.add(demande);
        String ref = AgregateurSimule.nouvelleReference();
        Distante d = new Distante();
        d.montant = demande.montant();
        transactions.put(ref, d);
        return new Initiation(ref, "Validez le paiement sur votre téléphone");
    }

    public synchronized EtatDistant statut(String ref) throws AgregateurException {
        verifications++;
        if (echecsVerification > 0) {
            echecsVerification--;
            throw new AgregateurException("Agrégateur injoignable (simulé)");
        }
        Distante d = transactions.get(ref);
        if (d == null) {
            throw new AgregateurException("Transaction inconnue");
        }
        return new EtatDistant(d.etat, d.referenceOperateur, d.montant);
    }

    /** Le locataire valide sur son téléphone (montant débité = montant demandé). */
    public synchronized void confirmer(String ref) {
        Distante d = transactions.get(ref);
        d.etat = Etat.SUCCES;
        d.referenceOperateur = "OP-" + ref.substring(ref.length() - 8);
    }

    /** L'opérateur annonce un autre montant que celui demandé. */
    public synchronized void confirmerAvecMontant(String ref, long montant) {
        confirmer(ref);
        transactions.get(ref).montant = montant;
    }

    public synchronized void echouer(String ref) {
        transactions.get(ref).etat = Etat.ECHEC;
    }

    public synchronized void echouerProchainesInitiations(int n) {
        echecsInitiation = n;
    }

    public synchronized void echouerProchainesVerifications(int n) {
        echecsVerification = n;
    }

    public synchronized List<Demande> demandes() {
        return new ArrayList<>(demandes);
    }

    public synchronized int verifications() {
        return verifications;
    }

    public synchronized String derniereReference() {
        return transactions.keySet().stream().reduce((a, b) -> b).orElseThrow();
    }

    public synchronized void reinitialiser() {
        transactions.clear();
        demandes.clear();
        echecsInitiation = 0;
        echecsVerification = 0;
        verifications = 0;
    }
}
