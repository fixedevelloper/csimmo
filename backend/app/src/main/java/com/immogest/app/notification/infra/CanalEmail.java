package com.immogest.app.notification.infra;

import com.immogest.app.notification.TypeCanal;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/** E-mail par SMTP, serveur saisi par le client (STARTTLS par défaut). */
@Component
@ConditionalOnProperty(name = "immogest.notification.simuler", havingValue = "false", matchIfMissing = true)
public class CanalEmail implements CanalNotification {

    @Override
    public TypeCanal type() {
        return TypeCanal.EMAIL;
    }

    @Override
    public List<Champ> champs() {
        return List.of(new Champ("hote", false, true, null), new Champ("port", false, false, "587"),
                new Champ("utilisateur", false, false, null), new Champ("motDePasse", true, false, null),
                new Champ("expediteur", false, true, null), new Champ("starttls", false, false, "true"));
    }

    /** Fabrique du client SMTP, séparée pour être testée sans réseau. */
    JavaMailSenderImpl fabrique(Map<String, String> config) throws EnvoiException {
        ValidationUrl.verifierHote(config.get("hote"));
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(config.get("hote"));
        sender.setPort(Integer.parseInt(config.getOrDefault("port", "").isBlank() ? "587" : config.get("port").strip()));
        sender.setDefaultEncoding(StandardCharsets.UTF_8.name());
        boolean auth = !config.getOrDefault("utilisateur", "").isBlank();
        if (auth) {
            sender.setUsername(config.get("utilisateur"));
            sender.setPassword(config.getOrDefault("motDePasse", ""));
        }
        Properties p = sender.getJavaMailProperties();
        p.put("mail.smtp.auth", String.valueOf(auth));
        p.put("mail.smtp.starttls.enable", String.valueOf(!"false".equalsIgnoreCase(config.getOrDefault("starttls", "true"))));
        p.put("mail.smtp.connectiontimeout", "10000");
        p.put("mail.smtp.timeout", "20000");
        p.put("mail.smtp.writetimeout", "20000");
        return sender;
    }

    @Override
    public void envoyer(Envoi envoi, Map<String, String> config) throws EnvoiException {
        try {
            JavaMailSenderImpl sender = fabrique(config);
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            h.setFrom(config.get("expediteur"));
            h.setTo(envoi.destinataire());
            h.setSubject(envoi.sujet() == null ? "ImmoGest" : envoi.sujet());
            h.setText(envoi.corps(), false);
            sender.send(message);
        } catch (EnvoiException e) {
            throw e;
        } catch (Exception e) {
            throw new EnvoiException("Envoi SMTP impossible : " + e.getClass().getSimpleName(), e);
        }
    }
}
