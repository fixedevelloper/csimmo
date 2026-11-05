package com.immogest.licensing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Identifiant stable de la machine, première moitié de l'empreinte serveur. Sources, par priorité :
 * <ol>
 *   <li>variable {@code IMMOGEST_MACHINE_ID} (obligatoire dans Docker, où {@code /etc/machine-id} est celui du
 *       conteneur : {@code install.sh} y place l'identifiant de l'hôte) ;</li>
 *   <li>Linux : {@code /etc/machine-id}, puis {@code /var/lib/dbus/machine-id} ;</li>
 *   <li>Windows : {@code MachineGuid} du registre.</li>
 * </ol>
 */
public final class IdentifiantMachine {

    public static final String VARIABLE = "IMMOGEST_MACHINE_ID";
    static final List<Path> FICHIERS_LINUX = List.of(Path.of("/etc/machine-id"), Path.of("/var/lib/dbus/machine-id"));
    private static final Pattern MACHINE_GUID = Pattern.compile("MachineGuid\\s+REG_SZ\\s+(\\S+)");

    private IdentifiantMachine() {
    }

    public static Optional<String> detecter() {
        Optional<String> trouve = detecter(System::getenv, FICHIERS_LINUX);
        if (trouve.isPresent() || !System.getProperty("os.name", "").toLowerCase().contains("win")) {
            return trouve;
        }
        return lireRegistreWindows();
    }

    static Optional<String> detecter(Function<String, String> environnement, List<Path> fichiers) {
        String variable = environnement.apply(VARIABLE);
        if (variable != null && !variable.isBlank()) {
            return Optional.of(variable.strip());
        }
        for (Path fichier : fichiers) {
            try {
                if (Files.isReadable(fichier)) {
                    String contenu = Files.readString(fichier).strip();
                    if (!contenu.isEmpty()) {
                        return Optional.of(contenu);
                    }
                }
            } catch (IOException e) {
                // source suivante
            }
        }
        return Optional.empty();
    }

    /** Extrait le GUID de la sortie de {@code reg query}. */
    static Optional<String> extraireMachineGuid(String sortie) {
        Matcher m = MACHINE_GUID.matcher(sortie);
        return m.find() ? Optional.of(m.group(1)) : Optional.empty();
    }

    private static Optional<String> lireRegistreWindows() {
        try {
            Process p = new ProcessBuilder("reg", "query", "HKLM\\SOFTWARE\\Microsoft\\Cryptography", "/v", "MachineGuid")
                    .redirectErrorStream(true).start();
            String sortie = new String(p.getInputStream().readAllBytes());
            if (!p.waitFor(5, TimeUnit.SECONDS)) {
                p.destroyForcibly();
                return Optional.empty();
            }
            return extraireMachineGuid(sortie);
        } catch (IOException e) {
            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }
}
