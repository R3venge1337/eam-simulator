package com.eam_simulator.common;

import com.eam_simulator.engine.exceptions.DomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@Slf4j
@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<CustomErrorResponse> handleDomainException(DomainException ex) {
        log.warn("Wykryto błąd biznesowy domeny: [{}] - {}", ex.getErrorCode(), ex.getMessage());

        CustomErrorResponse error = new CustomErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                Instant.now()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
