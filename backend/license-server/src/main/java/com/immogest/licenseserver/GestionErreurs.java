package com.immogest.licenseserver;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class GestionErreurs {

    private static ProblemDetail pb(HttpStatus s, String code, String detail) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(s, detail);
        p.setProperty("code", code);
        return p;
    }

    @ExceptionHandler(ErreurMetier.class)
    ProblemDetail metier(ErreurMetier e) {
        return pb(e.statut(), e.code(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validation(MethodArgumentNotValidException e) {
        return pb(HttpStatus.BAD_REQUEST, "VALIDATION", "Requête invalide");
    }

    /** Contenu refusé par {@code Licence} (dates, palier, texte vide…). */
    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail illegal(IllegalArgumentException e) {
        return pb(HttpStatus.BAD_REQUEST, "LICENCE_INVALIDE", e.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    ProblemDetail doublon(DuplicateKeyException e) {
        return pb(HttpStatus.CONFLICT, "DOUBLON", "Cet enregistrement existe déjà");
    }
}
