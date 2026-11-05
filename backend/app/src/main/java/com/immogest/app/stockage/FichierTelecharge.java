package com.immogest.app.stockage;

import java.io.InputStream;
import java.net.URI;
import java.util.Optional;

/** Contenu prêt à être servi : soit un flux (déchiffré si besoin), soit une URL présignée de redirection. */
public record FichierTelecharge(FichierDto fichier, InputStream flux, Optional<URI> redirection) {
}
