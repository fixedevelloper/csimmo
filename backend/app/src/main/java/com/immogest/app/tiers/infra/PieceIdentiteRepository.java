package com.immogest.app.tiers.infra;

import com.immogest.app.tiers.domain.PieceIdentite;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PieceIdentiteRepository extends JpaRepository<PieceIdentite, UUID> {

    java.util.List<PieceIdentite> findByCibleTypeAndCibleIdOrderByCreatedAtAsc(com.immogest.app.tiers.CiblePiece type, UUID cibleId);
}
