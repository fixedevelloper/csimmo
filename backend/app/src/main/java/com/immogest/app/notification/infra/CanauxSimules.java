package com.immogest.app.notification.infra;

import com.immogest.app.notification.TypeCanal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Canaux simulés ({@code immogest.notification.simuler=true}) : aucun appel externe, les envois sont gardés en mémoire pour les tests
 * et seul le canal est journalisé (jamais le contenu). Un canal peut être programmé pour échouer N fois.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "immogest.notification.simuler", havingValue = "true")
public class CanauxSimules {

    private static final Logger LOG = LoggerFactory.getLogger(CanauxSimules.class);

    /** Registre des envois simulés. */
    public static class Registre {
        public record Trace(TypeCanal canal, CanalNotification.Envoi envoi, Map<String, String> config) {
        }

        private final List<Trace> envois = new CopyOnWriteArrayList<>();
        private final Map<TypeCanal, Integer> echecsRestants = new EnumMap<>(TypeCanal.class);
        private final Map<TypeCanal, Integer> tentatives = new EnumMap<>(TypeCanal.class);

        public List<Trace> envois() {
            return new ArrayList<>(envois);
        }

        public synchronized void echouerProchains(TypeCanal canal, int n) {
            echecsRestants.put(canal, n);
        }

        public synchronized int tentatives(TypeCanal canal) {
            return tentatives.getOrDefault(canal, 0);
        }

        public synchronized void reinitialiser() {
            envois.clear();
            echecsRestants.clear();
            tentatives.clear();
        }

        synchronized boolean doitEchouer(TypeCanal canal) {
            tentatives.merge(canal, 1, Integer::sum);
            int restant = echecsRestants.getOrDefault(canal, 0);
            if (restant > 0) {
                echecsRestants.put(canal, restant - 1);
                return true;
            }
            return false;
        }
    }

    @Bean
    Registre registreSimule() {
        return new Registre();
    }

    @Bean
    CanalNotification smsSimule(Registre r) {
        return new Simule(TypeCanal.SMS, r, List.of(new CanalNotification.Champ("url", false, true, null)));
    }

    @Bean
    CanalNotification whatsappSimule(Registre r) {
        return new Simule(TypeCanal.WHATSAPP, r, List.of(new CanalNotification.Champ("phoneNumberId", false, true, null),
                new CanalNotification.Champ("token", true, true, null)));
    }

    @Bean
    CanalNotification emailSimule(Registre r) {
        return new Simule(TypeCanal.EMAIL, r, List.of(new CanalNotification.Champ("hote", false, true, null),
                new CanalNotification.Champ("expediteur", false, true, null)));
    }

    @Bean
    CanalNotification pushSimule(Registre r) {
        return new Simule(TypeCanal.PUSH, r, List.of(new CanalNotification.Champ("projectId", false, true, null),
                new CanalNotification.Champ("compteService", true, true, null), new CanalNotification.Champ("apiKey", false, false, null),
                new CanalNotification.Champ("appId", false, false, null), new CanalNotification.Champ("messagingSenderId", false, false, null)));
    }

    private record Simule(TypeCanal type, Registre registre, List<Champ> champs) implements CanalNotification {
        @Override
        public void envoyer(Envoi envoi, Map<String, String> config) throws EnvoiException {
            if (registre.doitEchouer(type)) {
                throw new EnvoiException("Échec simulé");
            }
            registre.envois.add(new Registre.Trace(type, envoi, config));
            LOG.info("[notification simulée] canal {}", type);
        }
    }
}
