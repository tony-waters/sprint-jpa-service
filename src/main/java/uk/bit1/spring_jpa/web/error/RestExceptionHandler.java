package uk.bit1.spring_jpa.web.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import uk.bit1.spring_jpa.service.NotFoundException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFound(NotFoundException ex, HttpServletRequest req) {
        return new ApiError(
                Instant.now(),
                404,
                "NOT_FOUND",
                ex.getMessage(),
                req.getRequestURI(),
                Map.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError badRequest(IllegalArgumentException ex, HttpServletRequest req) {
        return new ApiError(
                Instant.now(),
                400,
                "BAD_REQUEST",
                ex.getMessage(),
                req.getRequestURI(),
                Map.of());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError conflict(IllegalStateException ex, HttpServletRequest req) {
        return new ApiError(
                Instant.now(),
                409,
                "CONFLICT",
                ex.getMessage(),
                req.getRequestURI(),
                Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError integrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        return new ApiError(
                Instant.now(),
                409,
                "DATA_INTEGRITY_VIOLATION",
                "Database constraint violated",
                req.getRequestURI(),
                Map.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> fieldErrors.put(fe.getField(), fe.getDefaultMessage()));
        return new ApiError(
                Instant.now(),
                400,
                "VALIDATION_ERROR",
                "Request validation failed",
                req.getRequestURI(),
                fieldErrors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError unreadableJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return new ApiError(
                Instant.now(),
                400,
                "INVALID_JSON",
                "Malformed JSON request body",
                req.getRequestURI(),
                Map.of()
        );
    }
}