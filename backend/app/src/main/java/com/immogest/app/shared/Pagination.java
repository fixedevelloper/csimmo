package com.immogest.app.shared;

import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/** Paramètres de pagination standard (CLAUDE.md §5.3) : {@code page} (à partir de 0), {@code size} (max 100), {@code sort=champ[,asc|desc]}. */
public final class Pagination {

    private Pagination() {
    }

    /**
     * @param autorises champs triables (liste blanche : jamais de tri sur un champ arbitraire fourni par le client)
     * @param defaut    champ de tri par défaut
     */
    public static Pageable de(int page, int size, String sort, Set<String> autorises, String defaut) {
        String champ = defaut;
        Sort.Direction direction = Sort.Direction.ASC;
        if (sort != null && !sort.isBlank()) {
            String[] parties = sort.split(",", 2);
            if (autorises.contains(parties[0].strip())) {
                champ = parties[0].strip();
            }
            if (parties.length == 2 && "desc".equalsIgnoreCase(parties[1].strip())) {
                direction = Sort.Direction.DESC;
            }
        }
        Sort tri = Sort.by(direction, champ);
        // Départage stable : l'ordre ne doit pas changer d'une page à l'autre pour des valeurs égales
        tri = tri.and(Sort.by(Sort.Direction.ASC, "id"));
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), tri);
    }
}
