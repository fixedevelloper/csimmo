package com.immogest.app.shared.web;

import com.immogest.app.shared.ErreurMetier;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** Toutes les erreurs sont renvoyées en {@link ProblemDetail} (RFC 7807) avec un champ {@code code} stable. */
@RestControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ErreurMetier.class)
    ProblemDetail erreurMetier(ErreurMetier e) {
        ProblemDetail pb = ProblemDetail.forStatusAndDetail(e.status(), e.getMessage());
        pb.setProperty("code", e.code());
        return pb;
    }

    /** Refus levé par la sécurité de méthode ({@code @PreAuthorize}) : 403 et non 500. */
    @ExceptionHandler(AccessDeniedException.class)
    ProblemDetail accesRefuse(AccessDeniedException e) {
        ProblemDetail pb = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Accès refusé");
        pb.setProperty("code", "ACCES_REFUSE");
        return pb;
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail erreurInattendue(Exception e) {
        LOG.error("Erreur inattendue", e);
        ProblemDetail pb = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne");
        pb.setProperty("code", "ERREUR_INTERNE");
        return pb;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail pb = ProblemDetail.forStatusAndDetail(status, "Données invalides");
        pb.setProperty("code", "VALIDATION_ERREUR");
        List<String> champs = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage()).toList();
        pb.setProperty("erreurs", champs);
        return handleExceptionInternal(ex, pb, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {
        Object corps = body;
        if (corps == null && ex instanceof ErrorResponse reponse) {
            corps = reponse.updateAndGetBody(getMessageSource(), LocaleContextHolder.getLocale());
        }
        if (corps instanceof ProblemDetail pb && (pb.getProperties() == null || !pb.getProperties().containsKey("code"))) {
            pb.setProperty("code", "HTTP_" + statusCode.value());
        }
        return super.handleExceptionInternal(ex, corps, headers, statusCode, request);
    }
}
