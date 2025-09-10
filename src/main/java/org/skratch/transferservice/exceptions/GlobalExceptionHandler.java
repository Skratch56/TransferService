package org.skratch.transferservice.exceptions;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.skratch.transferservice.dto.ApiError;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
@Hidden
public class GlobalExceptionHandler {

    private ApiError buildError(HttpStatus status, String message, HttpServletRequest request) {
        return new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        return new ResponseEntity<>(buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
