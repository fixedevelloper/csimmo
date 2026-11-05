package com.immogest.app.bail.infra;

import com.immogest.app.shared.ErreurMetier;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Bail en PDF : mise en page Thymeleaf FIXE (classpath, de confiance) rendue en XHTML puis en PDF par OpenHTMLtoPDF.
 * Les textes modifiables par l'administrateur n'y arrivent que comme données, échappées : jamais comme gabarit.
 */
@Component
public class GenerateurBailPdf {

    private final TemplateEngine moteur;

    GenerateurBailPdf() {
        ClassLoaderTemplateResolver resolveur = new ClassLoaderTemplateResolver();
        resolveur.setPrefix("templates/");
        resolveur.setSuffix(".xhtml");
        resolveur.setTemplateMode(TemplateMode.XML);
        resolveur.setCharacterEncoding("UTF-8");
        resolveur.setCacheable(true);
        moteur = new TemplateEngine();
        moteur.setTemplateResolver(resolveur);
    }

    public byte[] generer(DonneesBail donnees) {
        Context contexte = new Context();
        contexte.setVariable("d", donnees);
        String xhtml = moteur.process("bail/bail", contexte);
        try (ByteArrayOutputStream sortie = new ByteArrayOutputStream()) {
            PdfRendererBuilder constructeur = new PdfRendererBuilder();
            constructeur.useFastMode();
            constructeur.withHtmlContent(xhtml, null);
            constructeur.toStream(sortie);
            constructeur.run();
            return sortie.toByteArray();
        } catch (Exception e) {
            throw new ErreurMetier("BAIL_PDF_ECHEC", "Génération du PDF impossible", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
