package com.immogest.app.finance;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @param delaiGraceJours jours après l'échéance avant EN_RETARD (défaut 5)
 * @param horizonJours    une échéance est créée dès qu'elle tombe dans les N prochains jours (défaut 30)
 */
@ConfigurationProperties("immogest.finance")
public record FinanceProperties(Integer delaiGraceJours, Integer horizonJours) {

    public FinanceProperties {
        delaiGraceJours = delaiGraceJours == null ? 5 : delaiGraceJours;
        horizonJours = horizonJours == null ? 30 : horizonJours;
    }
}
