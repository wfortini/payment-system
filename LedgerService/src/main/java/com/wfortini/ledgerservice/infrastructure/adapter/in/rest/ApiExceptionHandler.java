package com.wfortini.ledgerservice.infrastructure.adapter.in.rest;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(LedgerEntryNotFoundException.class)
    ProblemDetail handleNotFound(LedgerEntryNotFoundException exception) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Ledger entry not found");
        problem.setType(URI.create("urn:problem:ledger-entry-not-found"));
        return problem;
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class
    })
    ProblemDetail handleBadRequest(Exception exception) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid ledger request");
        problem.setTitle("Validation failed");
        problem.setType(URI.create("urn:problem:validation-failed"));
        return problem;
    }
}
