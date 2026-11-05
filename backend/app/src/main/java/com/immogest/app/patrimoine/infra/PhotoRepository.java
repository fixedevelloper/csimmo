package com.immogest.app.patrimoine.infra;

import com.immogest.app.patrimoine.CibleTypePhoto;
import com.immogest.app.patrimoine.domain.PhotoPatrimoine;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<PhotoPatrimoine, UUID> {

    List<PhotoPatrimoine> findByCibleTypeAndCibleIdOrderByOrdreAscCreatedAtAsc(CibleTypePhoto type, UUID cibleId);

    long countByCibleTypeAndCibleId(CibleTypePhoto type, UUID cibleId);
}
