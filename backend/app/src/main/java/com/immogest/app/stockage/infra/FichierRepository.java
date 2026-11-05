package com.immogest.app.stockage.infra;

import com.immogest.app.stockage.domain.Fichier;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FichierRepository extends JpaRepository<Fichier, UUID> {
}
