package com.immogest.app.shared;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Rendu PDF commun (Thymeleaf puis OpenHTMLtoPDF). Les gabarits sont des ressources du classpath, donc de confiance ; les données
 * qu'on leur passe sont du texte brut, échappé à l'affichage : un texte saisi par un utilisateur n'est jamais un gabarit.
 */
@Component
public class RenduPdf {

    private final TemplateEngine moteur;

    RenduPdf() {
        ClassLoaderTemplateResolver resolveur = new ClassLoaderTemplateResolver();
        resolveur.setPrefix("templates/");
        resolveur.setSuffix(".xhtml");
        resolveur.setTemplateMode(TemplateMode.XML);
        resolveur.setCharacterEncoding("UTF-8");
        resolveur.setCacheable(true);
        moteur = new TemplateEngine();
        moteur.setTemplateResolver(resolveur);
    }

    /** @param gabarit chemin sous {@code templates/}, sans extension (ex. {@code relance/mise-en-demeure}) ; la variable s'appelle {@code d} */
    public byte[] rendre(String gabarit, Object donnees) {
        Context contexte = new Context();
        contexte.setVariable("d", donnees);
        String xhtml = moteur.process(gabarit, contexte);
        try (ByteArrayOutputStream sortie = new ByteArrayOutputStream()) {
            PdfRendererBuilder constructeur = new PdfRendererBuilder();
            constructeur.useFastMode();
            constructeur.withHtmlContent(xhtml, null);
            constructeur.toStream(sortie);
            constructeur.run();
            return sortie.toByteArray();
        } catch (Exception e) {
            throw new ErreurMetier("DOCUMENT_PDF_ECHEC", "Génération du PDF impossible", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
