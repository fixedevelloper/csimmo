package com.immogest.licenseserver;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("license-server")
public record Proprietes(String clePriveeChemin, String jwtSecret, String adminEmail, String adminMotDePasse, int transfertsMax) {
}
