package com.immogest.app.stockage;

import com.immogest.app.SeaweedFsTestConfiguration;
import com.immogest.app.TestcontainersConfiguration;
import com.immogest.app.shared.Acteur;
import com.immogest.app.shared.ControleAccesFichier;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

/** Beans de test : un module propriétaire {@code test} dont seul le rôle LECTEUR peut lire, et l'acteur courant. */
@TestConfiguration(proxyBeanMethods = false)
@Import({TestcontainersConfiguration.class, SeaweedFsTestConfiguration.class})
public class StockageTestSupport {

    public static Acteur lecteur() {
        return new Acteur(UUID.randomUUID(), java.util.Set.of("LECTEUR"));
    }

    public static Acteur intrus() {
        return new Acteur(UUID.randomUUID(), java.util.Set.of("AUTRE"));
    }

    @Bean
    ControleAccesFichier controleAccesTest() {
        return new ControleAccesFichier() {
            @Override
            public String module() {
                return "test";
            }

            @Override
            public boolean peutLire(String entite, UUID entiteId, Acteur acteur) {
                return acteur.aLeRole("LECTEUR");
            }
        };
    }

    /** Requête authentifiée avec le rôle LECTEUR (accepté par le contrôle d'accès de test). */
    public static RequestPostProcessor jwtLecteur() {
        return jwt().jwt(j -> j.subject(UUID.randomUUID().toString()))
                .authorities(new SimpleGrantedAuthority("ROLE_LECTEUR"));
    }

    /** Requête authentifiée sans le rôle LECTEUR. */
    public static RequestPostProcessor jwtIntrus() {
        return jwt().jwt(j -> j.subject(UUID.randomUUID().toString()))
                .authorities(new SimpleGrantedAuthority("ROLE_AUTRE"));
    }

    public static byte[] png(int largeur, int hauteur) throws IOException {
        var image = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_RGB);
        var sortie = new ByteArrayOutputStream();
        ImageIO.write(image, "png", sortie);
        return sortie.toByteArray();
    }

    public static byte[] pdf() {
        return "%PDF-1.4\n1 0 obj<</Type/Catalog>>endobj\ntrailer<</Root 1 0 R>>\n%%EOF\n"
                .getBytes(StandardCharsets.ISO_8859_1);
    }
}
