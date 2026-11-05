package com.immogest.app.paiementenligne.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.paiementenligne.OperateurMobile;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Agrégateur simulé ({@code immogest.paiementenligne.simuler=true}) : aucun appel externe. Il applique un vrai schéma de signature
 * (HMAC-SHA256 de « horodatage.corps », tolérance de 5 minutes, comparaison en temps constant) pour que les tests de webhook falsifié ou
 * rejoué aient un sens. Un agrégateur réel a son propre schéma, documenté par le fournisseur.
 */
@Component
@ConditionalOnProperty(name = "immogest.paiementenligne.simuler", havingValue = "true")
public class AgregateurSimule implements AgregateurPaiement {

    public static final String CODE = "simule";
    static final long TOLERANCE_SECONDES = 300;
    private static final ObjectMapper JSON = new ObjectMapper();

    private final RegistreSimule registre;
    private final Clock horloge;

    AgregateurSimule(RegistreSimule registre, Clock horloge) {
        this.registre = registre;
        this.horloge = horloge;
    }

    @Override
    public String code() {
        return CODE;
    }

    @Override
    public List<Champ> champs() {
        return List.of(new Champ("identifiantMarchand", false, true), new Champ("secretWebhook", true, true));
    }

    @Override
    public Initiation initier(Demande demande, Map<String, String> config) throws AgregateurException {
        return registre.initier(demande);
    }

    @Override
    public EtatDistant verifierStatut(String referenceFournisseur, Map<String, String> config) throws AgregateurException {
        return registre.statut(referenceFournisseur);
    }

    @Override
    public Optional<EvenementWebhook> validerWebhook(Map<String, String> entetes, byte[] corps, Map<String, String> config) throws SignatureInvalideException {
        String signature = entetes.get("x-sim-signature");
        String horodatage = entetes.get("x-sim-timestamp");
        String secret = config.get("secretWebhook");
        if (signature == null || horodatage == null || secret == null || secret.isBlank()) {
            throw new SignatureInvalideException("Signature absente");
        }
        long t;
        try {
            t = Long.parseLong(horodatage);
        } catch (NumberFormatException e) {
            throw new SignatureInvalideException("Horodatage illisible");
        }
        if (Math.abs(horloge.instant().getEpochSecond() - t) > TOLERANCE_SECONDES) {
            throw new SignatureInvalideException("Horodatage hors tolérance (rejeu ?)");
        }
        byte[] attendue = hmac(secret, horodatage, corps).getBytes(StandardCharsets.US_ASCII);
        if (!MessageDigest.isEqual(attendue, signature.strip().toLowerCase().getBytes(StandardCharsets.US_ASCII))) {
            throw new SignatureInvalideException("Signature incorrecte");
        }
        try {
            JsonNode n = JSON.readTree(corps);
            String evenement = n.path("id").asText("");
            String reference = n.path("reference").asText("");
            if (evenement.isBlank() || reference.isBlank()) {
                return Optional.empty();
            }
            Etat etat = switch (n.path("statut").asText("")) {
                case "SUCCES" -> Etat.SUCCES;
                case "ECHEC" -> Etat.ECHEC;
                default -> Etat.EN_ATTENTE;
            };
            Long montant = n.hasNonNull("montant") ? n.get("montant").asLong() : null;
            return Optional.of(new EvenementWebhook(evenement, reference, new EtatDistant(etat, n.path("referenceOperateur").asText(null), montant)));
        } catch (java.io.IOException e) {
            return Optional.empty(); // authentique mais illisible : sans effet
        }
    }

    /** Signature attendue d'un corps (utilisée aussi par les tests pour fabriquer de vrais webhooks). */
    public static String hmac(String secret, String horodatage, byte[] corps) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            mac.update((horodatage + ".").getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(mac.doFinal(corps));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Fabrique de références simulées. */
    static String nouvelleReference() {
        return "SIM-" + UUID.randomUUID();
    }

    static OperateurMobile operateurParDefaut() {
        return OperateurMobile.MTN_MOMO;
    }
}
