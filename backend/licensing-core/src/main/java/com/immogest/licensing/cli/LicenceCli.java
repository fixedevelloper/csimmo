package com.immogest.licensing.cli;

import com.immogest.licensing.Ed25519;
import com.immogest.licensing.EmpreinteServeur;
import com.immogest.licensing.Licence;
import com.immogest.licensing.LicenceSignee;
import com.immogest.licensing.ResultatVerification;
import com.immogest.licensing.VerificateurLicence;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.KeyPair;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Outil de développement et de support : génère une paire de clés, signe une licence, la vérifie, calcule une empreinte.
 *
 * <pre>
 * ./mvnw -q -pl licensing-core exec:java -Dexec.args="generer-cles --sortie /chemin/hors/depot"
 * </pre>
 *
 * La clé privée ne doit JAMAIS être commitée ni copiée chez un client ; le serveur de licences (étape 9.1) la lit
 * depuis un fichier protégé. Codes de sortie : 0 succès, 1 échec ou licence non valide, 2 usage incorrect.
 */
public final class LicenceCli {

    public static final int OK = 0;
    public static final int ECHEC = 1;
    public static final int USAGE = 2;

    static final String CLE_PRIVEE = "licence-private.key";
    static final String CLE_PUBLIQUE = "licence-public.key";

    private LicenceCli() {
    }

    public static void main(String[] args) {
        System.exit(executer(args, System.out, System.err));
    }

    public static int executer(String[] args, PrintStream out, PrintStream err) {
        if (args.length == 0) {
            usage(err);
            return USAGE;
        }
        try {
            Map<String, String> options = options(Arrays.copyOfRange(args, 1, args.length));
            return switch (args[0]) {
                case "generer-cles" -> genererCles(options, out);
                case "signer" -> signer(options, out);
                case "verifier" -> verifier(options, out);
                case "empreinte" -> empreinte(options, out);
                default -> {
                    err.println("Commande inconnue : " + args[0]);
                    usage(err);
                    yield USAGE;
                }
            };
        } catch (UsageException e) {
            err.println("Erreur : " + e.getMessage());
            usage(err);
            return USAGE;
        } catch (IOException | IllegalArgumentException e) {
            err.println("Échec : " + e.getMessage());
            return ECHEC;
        }
    }

    // ------------------------------------------------------------------ commandes

    private static int genererCles(Map<String, String> o, PrintStream out) throws IOException {
        Path dossier = Path.of(exiger(o, "sortie"));
        Files.createDirectories(dossier);
        Path privee = dossier.resolve(CLE_PRIVEE);
        Path publique = dossier.resolve(CLE_PUBLIQUE);
        if (Files.exists(privee) || Files.exists(publique)) {
            throw new IOException("des clés existent déjà dans " + dossier + " (jamais écrasées)");
        }
        KeyPair paire = Ed25519.genererPaire();
        ecrireNouveau(privee, Ed25519.encoder(paire.getPrivate()) + "\n", true);
        ecrireNouveau(publique, Ed25519.encoder(paire.getPublic()) + "\n", false);
        out.println("Clé privée  : " + privee + "  (secrète : à garder chez l'éditeur, hors dépôt)");
        out.println("Clé publique : " + publique + "  (à embarquer dans l'application)");
        return OK;
    }

    private static int signer(Map<String, String> o, PrintStream out) throws IOException {
        Licence licence = new Licence(
                o.getOrDefault("id", "L-" + UUID.randomUUID()),
                exiger(o, "client"),
                entier(exiger(o, "palier")),
                date(exiger(o, "debut"), "debut"),
                date(exiger(o, "expiration"), "expiration"),
                exiger(o, "empreinte"),
                o.containsKey("fonctionnalites") ? List.of(o.get("fonctionnalites").split("\\s*,\\s*")) : List.of());
        var privee = Ed25519.decoderClePrivee(Files.readString(Path.of(exiger(o, "cle-privee"))));
        LicenceSignee signee = LicenceSignee.signer(licence, privee);
        ecrireNouveau(Path.of(exiger(o, "sortie")), signee.versJson(), false);
        out.println("Licence " + licence.licenseId() + " signée pour « " + licence.client() + " » ("
                + licence.palierLots() + " lots, du " + licence.dateDebut() + " au " + licence.dateExpiration()
                + ") : " + o.get("sortie"));
        return OK;
    }

    private static int verifier(Map<String, String> o, PrintStream out) throws IOException {
        var publique = Ed25519.decoderClePublique(Files.readString(Path.of(exiger(o, "cle-publique"))));
        String contenu = Files.readString(Path.of(exiger(o, "fichier")), StandardCharsets.UTF_8);
        LocalDate jour = o.containsKey("date") ? date(o.get("date"), "date") : LocalDate.now(ZoneId.of("Africa/Douala"));
        ResultatVerification r = VerificateurLicence.verifier(contenu, publique, exiger(o, "empreinte"), jour);
        out.println("Statut : " + r.statut() + (r.detail() == null ? "" : " (" + r.detail() + ")"));
        if (r.licence() != null) {
            Licence l = r.licence();
            out.println("Licence " + l.licenseId() + " — " + l.client() + " — " + l.palierLots() + " lots — du "
                    + l.dateDebut() + " au " + l.dateExpiration() + " — " + l.joursRestantsAu(jour) + " jour(s) restant(s)");
        }
        return r.estValide() ? OK : ECHEC;
    }

    private static int empreinte(Map<String, String> o, PrintStream out) {
        String installation = o.getOrDefault("installation-id", EmpreinteServeur.nouvelIdentifiantInstallation());
        out.println("Identifiant d'installation : " + installation);
        out.println("Empreinte du serveur       : " + EmpreinteServeur.calculerLocale(installation));
        return OK;
    }

    // ------------------------------------------------------------------ utilitaires

    /** Écrit un fichier qui ne doit pas exister ; la clé privée est créée en lecture/écriture pour son seul propriétaire. */
    private static void ecrireNouveau(Path chemin, String contenu, boolean secret) throws IOException {
        try {
            Files.writeString(chemin, contenu, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
        } catch (FileAlreadyExistsException e) {
            throw new IOException("le fichier existe déjà : " + chemin + " (jamais écrasé)");
        }
        if (secret) {
            try {
                Files.setPosixFilePermissions(chemin, PosixFilePermissions.fromString("rw-------"));
            } catch (UnsupportedOperationException e) {
                // Windows : les droits dépendent de l'ACL du dossier
            }
        }
    }

    private static Map<String, String> options(String[] args) {
        Map<String, String> options = new HashMap<>();
        Set<String> connues = Set.of("sortie", "cle-privee", "cle-publique", "client", "palier", "debut", "expiration",
                "empreinte", "fonctionnalites", "id", "fichier", "date", "installation-id");
        for (int i = 0; i < args.length; i += 2) {
            if (!args[i].startsWith("--") || i + 1 >= args.length) {
                throw new UsageException("option mal formée : " + args[i]);
            }
            String nom = args[i].substring(2);
            if (!connues.contains(nom)) {
                throw new UsageException("option inconnue : " + args[i]);
            }
            options.put(nom, args[i + 1]);
        }
        return options;
    }

    private static String exiger(Map<String, String> options, String nom) {
        String valeur = options.get(nom);
        if (valeur == null || valeur.isBlank()) {
            throw new UsageException("option obligatoire manquante : --" + nom);
        }
        return valeur;
    }

    private static int entier(String valeur) {
        try {
            return Integer.parseInt(valeur);
        } catch (NumberFormatException e) {
            throw new UsageException("--palier doit être un entier");
        }
    }

    private static LocalDate date(String valeur, String option) {
        try {
            return LocalDate.parse(valeur);
        } catch (java.time.format.DateTimeParseException e) {
            throw new UsageException("--" + option + " : date invalide (AAAA-MM-JJ attendu)");
        }
    }

    private static void usage(PrintStream err) {
        err.println("""
                Usage : LicenceCli <commande> [options]
                  generer-cles --sortie <dossier>
                  signer       --cle-privee <fichier> --client <nom> --palier <n> --debut <AAAA-MM-JJ>
                               --expiration <AAAA-MM-JJ> --empreinte <hex> [--fonctionnalites a,b] [--id <id>] --sortie <fichier.lic>
                  verifier     --cle-publique <fichier> --fichier <fichier.lic> --empreinte <hex> [--date <AAAA-MM-JJ>]
                  empreinte    [--installation-id <uuid>]""");
    }

    private static final class UsageException extends RuntimeException {
        UsageException(String message) {
            super(message);
        }
    }
}
