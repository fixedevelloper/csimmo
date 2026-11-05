package com.immogest.app.paiementenligne.infra;

import com.immogest.app.paiementenligne.domain.Fournisseur;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FournisseurRepository extends JpaRepository<Fournisseur, UUID> {

    Optional<Fournisseur> findByCode(String code);

    List<Fournisseur> findByActifTrue();
}
