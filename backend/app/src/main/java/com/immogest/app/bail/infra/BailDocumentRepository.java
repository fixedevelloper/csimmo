package com.immogest.app.bail.infra;

import com.immogest.app.bail.domain.BailDocument;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BailDocumentRepository extends JpaRepository<BailDocument, UUID> {

    List<BailDocument> findByBailIdOrderByNumeroVersionDesc(UUID bailId);

    int countByBailId(UUID bailId);
}
