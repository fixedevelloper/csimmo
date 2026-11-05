package com.immogest.licenseserver;

import static com.immogest.licenseserver.Modeles.*;

import jakarta.validation.Valid;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
class Api {

    private static final Duration VALIDITE_JETON = Duration.ofHours(8);
    private static final int ECHECS_MAX = 5;

    private final ServiceLicences service;
    private final JdbcTemplate jdbc;
    private final PasswordEncoder encodeur;
    private final JwtEncoder jwt;

    Api(ServiceLicences service, JdbcTemplate jdbc, PasswordEncoder encodeur, JwtEncoder jwt) {
        this.service = service;
        this.jdbc = jdbc;
        this.encodeur = encodeur;
        this.jwt = jwt;
    }

    // ------------------------------------------------------------------ connexion éditeur

    @PostMapping("/auth/login")
    JetonDto connexion(@Valid @RequestBody ConnexionRequete r) {
        var lignes = jdbc.queryForList("SELECT id, mot_de_passe_hash, echecs, verrouille_jusqu > now() AS verrouille FROM ls_editeur WHERE lower(email) = lower(?)", r.email().strip());
        ErreurMetier refus = new ErreurMetier(HttpStatus.UNAUTHORIZED, "IDENTIFIANTS_INVALIDES", "Identifiants invalides");
        if (lignes.isEmpty()) {
            encodeur.matches(r.motDePasse(), "$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5BIFo9yE3sT9mYb8yQ5W1m9m9m9mO"); // même durée que pour un compte existant
            throw refus;
        }
        var e = lignes.get(0);
        if (Boolean.TRUE.equals(e.get("verrouille"))) {
            throw new ErreurMetier(HttpStatus.TOO_MANY_REQUESTS, "COMPTE_VERROUILLE", "Compte verrouillé temporairement");
        }
        if (!encodeur.matches(r.motDePasse(), (String) e.get("mot_de_passe_hash"))) {
            jdbc.update("UPDATE ls_editeur SET echecs = echecs + 1, verrouille_jusqu = CASE WHEN echecs + 1 >= ? THEN now() + interval '15 minutes' ELSE verrouille_jusqu END WHERE id = ?",
                    ECHECS_MAX, e.get("id"));
            throw refus;
        }
        jdbc.update("UPDATE ls_editeur SET echecs = 0, verrouille_jusqu = NULL WHERE id = ?", e.get("id"));
        Instant maintenant = Instant.now();
        var claims = JwtClaimsSet.builder().subject(r.email().strip().toLowerCase()).issuedAt(maintenant).expiresAt(maintenant.plus(VALIDITE_JETON)).build();
        String jeton = jwt.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new JetonDto(jeton, VALIDITE_JETON.toSeconds());
    }

    // ------------------------------------------------------------------ clients

    @PostMapping("/clients")
    @ResponseStatus(HttpStatus.CREATED)
    ClientDto creerClient(@Valid @RequestBody ClientRequete r) {
        return service.creerClient(r);
    }

    @PutMapping("/clients/{id}")
    ClientDto modifierClient(@PathVariable UUID id, @Valid @RequestBody ClientRequete r) {
        return service.modifierClient(id, r);
    }

    @GetMapping("/clients/{id}")
    ClientDto client(@PathVariable UUID id) {
        return service.client(id);
    }

    @GetMapping("/clients")
    PageReponse<ClientDto> clients(@RequestParam(required = false) String q, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.clients(q, Math.max(page, 0), taille(size));
    }

    private static int taille(int size) {
        return Math.min(Math.max(size, 1), 100);
    }

    // ------------------------------------------------------------------ licences

    @PostMapping("/licences")
    @ResponseStatus(HttpStatus.CREATED)
    EmissionDto emettre(@Valid @RequestBody EmissionRequete r, Authentication a) {
        return service.emettre(r, a.getName());
    }

    @GetMapping("/licences")
    PageReponse<LicenceDto> licences(@RequestParam(required = false) UUID clientId, @RequestParam(required = false) String statut, @RequestParam(required = false) Integer expirantSous,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.licences(clientId, statut, expirantSous, Math.max(page, 0), taille(size));
    }

    @GetMapping("/licences/{id}")
    LicenceDto licence(@PathVariable UUID id) {
        return service.licence(id);
    }

    @GetMapping("/licences/{id}/emissions")
    java.util.List<HistoriqueEmissionDto> emissions(@PathVariable UUID id) {
        return service.historique(id);
    }

    @PostMapping("/licences/{id}/renouvellement")
    EmissionDto renouveler(@PathVariable UUID id, @Valid @RequestBody RenouvellementRequete r, Authentication a) {
        return service.renouveler(id, r, a.getName());
    }

    @PostMapping("/licences/{id}/transfert")
    EmissionDto transferer(@PathVariable UUID id, @Valid @RequestBody TransfertRequete r, Authentication a) {
        return service.transferer(id, r, a.getName());
    }

    @PostMapping("/licences/{id}/revocation")
    LicenceDto revoquer(@PathVariable UUID id, @Valid @RequestBody RevocationRequete r) {
        return service.revoquer(id, r.motif());
    }

    /** Dernier fichier {@code .lic} signé, à remettre ou à importer chez le client. */
    @GetMapping("/licences/{id}/fichier")
    ResponseEntity<String> fichier(@PathVariable UUID id) {
        String contenu = service.fichierCourant(id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("licence-" + id + ".lic").build().toString()).body(contenu);
    }

    // ------------------------------------------------------------------ paiements de licence

    @PostMapping("/licences/{id}/paiements")
    @ResponseStatus(HttpStatus.CREATED)
    PaiementDto enregistrerPaiement(@PathVariable UUID id, @Valid @RequestBody PaiementRequete r, Authentication a) {
        return service.enregistrerPaiement(id, r, a.getName());
    }

    @GetMapping("/licences/{id}/paiements")
    PageReponse<PaiementDto> paiements(@PathVariable UUID id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.paiements(id, Math.max(page, 0), taille(size));
    }

    // ------------------------------------------------------------------ versions

    @PostMapping("/versions")
    @ResponseStatus(HttpStatus.CREATED)
    VersionDto publier(@Valid @RequestBody VersionRequete r, Authentication a) {
        return service.publier(r, a.getName());
    }

    @GetMapping("/versions")
    PageReponse<VersionDto> versions(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.versions(Math.max(page, 0), taille(size));
    }

    /** Publique : lue par {@code update.sh} des installations. */
    @GetMapping("/versions/derniere")
    ResponseEntity<VersionDto> derniere() {
        return service.derniereVersion().map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // ------------------------------------------------------------------ vérification hebdomadaire (publique : l'empreinte et l'identifiant font foi)

    @PostMapping("/verification")
    VerificationReponse verification(@Valid @RequestBody VerificationRequete r) {
        return service.verifier(r);
    }
}
