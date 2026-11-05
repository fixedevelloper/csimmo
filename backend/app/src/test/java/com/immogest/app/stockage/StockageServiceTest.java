package com.immogest.app.stockage;

import static com.immogest.app.stockage.StockageTestSupport.intrus;
import static com.immogest.app.stockage.StockageTestSupport.lecteur;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.shared.FileStorage;
import com.immogest.app.stockage.infra.FichierRepository;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(StockageTestSupport.class)
class StockageServiceTest {

    @Autowired
    StockageService service;
    @Autowired
    FichierRepository repository;
    @Autowired
    FileStorage stockage;

    private FichierDto envoyer(byte[] contenu, String nom, boolean sensible) {
        return service.enregistrer("test", "dossier", UUID.randomUUID(), nom, new ByteArrayInputStream(contenu), sensible);
    }

    @Test
    void envoiPuisLecturePourUnActeurAutorise() throws Exception {
        byte[] pdf = StockageTestSupport.pdf();
        FichierDto fichier = envoyer(pdf, "contrat.pdf", false);

        assertThat(fichier.typeMime()).isEqualTo("application/pdf");
        try (var flux = service.telecharger(fichier.id(), lecteur()).flux()) {
            assertThat(flux.readAllBytes()).isEqualTo(pdf);
        }
    }

    @Test
    void cleObjetNeContientPasLeNomFourni() {
        FichierDto fichier = envoyer(StockageTestSupport.pdf(), "../../secret nom.pdf", false);
        String cle = repository.findById(fichier.id()).orElseThrow().getCle();

        assertThat(cle).matches("test/dossier/[0-9a-f-]{36}/[0-9a-f-]{36}\\.pdf");
        assertThat(fichier.nomOrigine()).doesNotContain("/");
    }

    @Test
    void fauxPdfRefuseSelonLeContenu() {
        byte[] texte = "ceci n'est pas un pdf".getBytes(StandardCharsets.UTF_8);

        assertThatThrownBy(() -> envoyer(texte, "faux.pdf", false))
                .isInstanceOfSatisfying(ErreurMetier.class, e -> assertThat(e.code()).isEqualTo("FICHIER_TYPE_INTERDIT"));
    }

    @Test
    void fichierDePlusDeDixMoRefuse() {
        byte[] gros = new byte[10 * 1024 * 1024 + 1];

        assertThatThrownBy(() -> envoyer(gros, "gros.pdf", false))
                .isInstanceOfSatisfying(ErreurMetier.class, e -> assertThat(e.code()).isEqualTo("FICHIER_TROP_VOLUMINEUX"));
    }

    @Test
    void imageRedimensionneeA1600Pixels() throws Exception {
        FichierDto fichier = envoyer(StockageTestSupport.png(3200, 1200), "photo.png", false);

        try (var flux = service.telecharger(fichier.id(), lecteur()).flux()) {
            var image = ImageIO.read(flux);
            assertThat(image.getWidth()).isEqualTo(1600);
            assertThat(image.getHeight()).isEqualTo(600);
        }
    }

    @Test
    void petiteImageNonModifiee() throws Exception {
        byte[] png = StockageTestSupport.png(400, 300);
        FichierDto fichier = envoyer(png, "petite.png", false);

        try (var flux = service.telecharger(fichier.id(), lecteur()).flux()) {
            assertThat(flux.readAllBytes()).isEqualTo(png);
        }
    }

    @Test
    void accesRefuseSansDroit() {
        FichierDto fichier = envoyer(StockageTestSupport.pdf(), "a.pdf", false);

        assertThatThrownBy(() -> service.telecharger(fichier.id(), intrus()))
                .isInstanceOfSatisfying(ErreurMetier.class, e -> assertThat(e.code()).isEqualTo("FICHIER_ACCES_REFUSE"));
    }

    @Test
    void fichierInconnuIntrouvable() {
        assertThatThrownBy(() -> service.telecharger(UUID.randomUUID(), lecteur()))
                .isInstanceOfSatisfying(ErreurMetier.class, e -> assertThat(e.code()).isEqualTo("FICHIER_INTROUVABLE"));
    }

    @Test
    void fichierSensibleIllisibleDansLeBucketMaisRestitueParLeService() throws Exception {
        byte[] pdf = StockageTestSupport.pdf();
        FichierDto fichier = envoyer(pdf, "cni.pdf", true);

        String cle = repository.findById(fichier.id()).orElseThrow().getCle();
        try (var brut = stockage.get(cle)) {
            byte[] dansLeBucket = brut.readAllBytes();
            assertThat(dansLeBucket).isNotEqualTo(pdf);
            assertThat(new String(dansLeBucket, StandardCharsets.ISO_8859_1)).doesNotContain("%PDF");
        }
        try (var flux = service.telecharger(fichier.id(), lecteur()).flux()) {
            assertThat(flux.readAllBytes()).isEqualTo(pdf);
        }
    }

    @Test
    void suppressionRetireObjetEtMetadonnees() {
        FichierDto fichier = envoyer(StockageTestSupport.pdf(), "a.pdf", false);
        String cle = repository.findById(fichier.id()).orElseThrow().getCle();

        service.supprimer(fichier.id());

        assertThat(repository.findById(fichier.id())).isEmpty();
        assertThatThrownBy(() -> stockage.get(cle)).isInstanceOf(RuntimeException.class);
    }
}
