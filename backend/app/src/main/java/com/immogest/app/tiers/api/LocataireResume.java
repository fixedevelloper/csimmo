package com.immogest.app.tiers.api;

import java.util.UUID;

public record LocataireResume(UUID id, String nom, String telephone, String email, boolean archive) {
}
