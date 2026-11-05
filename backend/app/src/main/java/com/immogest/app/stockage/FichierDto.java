package com.immogest.app.stockage;

import java.util.UUID;

public record FichierDto(UUID id, String nomOrigine, String typeMime, long taille) {
}
