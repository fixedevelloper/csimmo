package com.immogest.app.shared;

import java.util.List;
import org.springframework.data.domain.Page;

/** Réponse paginée standard : {@code { content, page, size, totalElements }}. */
public record PageReponse<T>(List<T> content, int page, int size, long totalElements) {

    public static <T> PageReponse<T> de(Page<T> page) {
        return new PageReponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
    }
}
