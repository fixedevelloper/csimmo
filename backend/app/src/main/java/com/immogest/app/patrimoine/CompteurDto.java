package com.immogest.app.patrimoine;

import java.util.UUID;

public record CompteurDto(UUID id, TypeCompteur type, String numero) {
}
