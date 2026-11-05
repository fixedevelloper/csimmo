package com.immogest.app.importation.web;

import com.immogest.app.importation.ImportService;
import com.immogest.app.importation.ModeleFichier;
import com.immogest.app.importation.PrevisualisationDto;
import com.immogest.app.importation.ResultatImportDto;
import com.immogest.app.importation.TypeImport;
import com.immogest.app.shared.ErreurMetier;
import java.io.IOException;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Import de données (ADMIN seul : opération en masse sur le patrimoine, les locataires et les baux). */
@RestController
@RequestMapping("/api/v1/import")
@PreAuthorize("hasRole('ADMIN')")
class ImportController {

    private static final long TAILLE_MAX = 10L * 1024 * 1024;
    private static final MediaType XLSX = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final ImportService service;

    ImportController(ImportService service) {
        this.service = service;
    }

    /** Modèle vide : {@code format=xlsx} (tous les onglets) ou {@code format=csv&type=…} (un seul type). */
    @GetMapping("/modele")
    ResponseEntity<byte[]> modele(@RequestParam(defaultValue = "xlsx") String format, @RequestParam(required = false) TypeImport type) {
        boolean csv = "csv".equalsIgnoreCase(format);
        if (!csv && !"xlsx".equalsIgnoreCase(format)) {
            throw new ErreurMetier("IMPORT_FORMAT_INCONNU", "Format inconnu : xlsx ou csv", HttpStatus.BAD_REQUEST);
        }
        if (csv && type == null) {
            throw new ErreurMetier("IMPORT_TYPE_REQUIS", "Un modèle CSV porte sur un type de données", HttpStatus.BAD_REQUEST);
        }
        byte[] contenu = csv ? ModeleFichier.csv(type) : ModeleFichier.xlsx();
        String nom = csv ? "modele-" + type.name().toLowerCase() + ".csv" : "modele-import-immogest.xlsx";
        return ResponseEntity.ok().contentType(csv ? MediaType.parseMediaType("text/csv;charset=UTF-8") : XLSX)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(nom).build().toString()).body(contenu);
    }

    @PostMapping(value = "/previsualisation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    PrevisualisationDto previsualiser(@RequestParam("fichier") MultipartFile fichier, @RequestParam(required = false) TypeImport type) throws IOException {
        return service.previsualiser(contenu(fichier), type);
    }

    /** Importe le fichier prévisualisé (même empreinte). 200 si importé ; 422 avec le rapport d'erreurs sinon (rien n'est écrit). */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ResultatImportDto> importer(@RequestParam("fichier") MultipartFile fichier, @RequestParam(required = false) TypeImport type, @RequestParam String empreinte)
            throws IOException {
        ResultatImportDto r = service.importer(contenu(fichier), type, empreinte);
        return ResponseEntity.status(r.importe() ? HttpStatus.OK : HttpStatus.UNPROCESSABLE_ENTITY).body(r);
    }

    private static byte[] contenu(MultipartFile fichier) throws IOException {
        if (fichier.isEmpty()) {
            throw new ErreurMetier("IMPORT_FICHIER_VIDE", "Le fichier est vide", HttpStatus.BAD_REQUEST);
        }
        if (fichier.getSize() > TAILLE_MAX) {
            throw new ErreurMetier("IMPORT_FICHIER_TROP_GROS", "Fichier trop volumineux (10 Mo au maximum)", HttpStatus.PAYLOAD_TOO_LARGE);
        }
        return fichier.getBytes();
    }
}
