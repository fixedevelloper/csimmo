package com.immogest.app.stockage.infra;

import com.immogest.app.shared.ErreurMetier;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import org.apache.tika.Tika;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** Vérifie le type réel du fichier (contenu, pas extension), sa taille, et redimensionne les images. */
@Component
public class ValidationFichier {

    public static final long TAILLE_MAX = 10L * 1024 * 1024;
    static final int DIMENSION_MAX = 1600;

    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp",
            "application/pdf", "pdf");
    private static final Set<String> IMAGES_REDIMENSIONNABLES = Set.of("image/jpeg", "image/png");

    private final Tika tika = new Tika();

    public record Valide(byte[] contenu, String typeMime, String extension) {
    }

    public Valide valider(byte[] contenu) {
        if (contenu.length == 0) {
            throw new ErreurMetier("FICHIER_VIDE", "Le fichier est vide", HttpStatus.BAD_REQUEST);
        }
        if (contenu.length > TAILLE_MAX) {
            throw new ErreurMetier("FICHIER_TROP_VOLUMINEUX", "Le fichier dépasse 10 Mo", HttpStatus.PAYLOAD_TOO_LARGE);
        }
        String type = tika.detect(contenu);
        String extension = EXTENSIONS.get(type);
        if (extension == null) {
            throw new ErreurMetier("FICHIER_TYPE_INTERDIT",
                    "Type de fichier non autorisé (jpeg, png, webp et pdf uniquement)", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        byte[] resultat = IMAGES_REDIMENSIONNABLES.contains(type) ? redimensionner(contenu, extension) : contenu;
        return new Valide(resultat, type, extension);
    }

    private byte[] redimensionner(byte[] contenu, String format) {
        try {
            var image = ImageIO.read(new ByteArrayInputStream(contenu));
            if (image == null) {
                throw new ErreurMetier("FICHIER_IMAGE_ILLISIBLE", "Image illisible", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            }
            int max = Math.max(image.getWidth(), image.getHeight());
            if (max <= DIMENSION_MAX) {
                return contenu;
            }
            double facteur = (double) DIMENSION_MAX / max;
            int largeur = Math.max(1, (int) Math.round(image.getWidth() * facteur));
            int hauteur = Math.max(1, (int) Math.round(image.getHeight() * facteur));
            int typeImage = "jpg".equals(format) ? java.awt.image.BufferedImage.TYPE_INT_RGB
                    : java.awt.image.BufferedImage.TYPE_INT_ARGB;
            var reduite = new java.awt.image.BufferedImage(largeur, hauteur, typeImage);
            var g = reduite.createGraphics();
            g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                    java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(image, 0, 0, largeur, hauteur, null);
            g.dispose();
            var sortie = new ByteArrayOutputStream();
            ImageIO.write(reduite, format, sortie);
            return sortie.toByteArray();
        } catch (IOException e) {
            throw new ErreurMetier("FICHIER_IMAGE_ILLISIBLE", "Image illisible", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
    }
}
