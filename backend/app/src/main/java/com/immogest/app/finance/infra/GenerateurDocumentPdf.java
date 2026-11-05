package com.immogest.app.finance.infra;

import com.immogest.app.shared.ErreurMetier;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/** Quittances et reçus en PDF : mise en page Thymeleaf fixe (classpath) rendue par OpenHTMLtoPDF. */
@Component
public class GenerateurDocumentPdf {

    private final TemplateEngine moteur;

    GenerateurDocumentPdf() {
        ClassLoaderTemplateResolver resolveur = new ClassLoaderTemplateResolver();
        resolveur.setPrefix("templates/");
        resolveur.setSuffix(".xhtml");
        resolveur.setTemplateMode(TemplateMode.XML);
        resolveur.setCharacterEncoding("UTF-8");
        resolveur.setCacheable(true);
        moteur = new TemplateEngine();
        moteur.setTemplateResolver(resolveur);
    }

    public byte[] generer(DonneesDocument donnees) {
        Context contexte = new Context();
        contexte.setVariable("d", donnees);
        String xhtml = moteur.process("finance/document", contexte);
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
