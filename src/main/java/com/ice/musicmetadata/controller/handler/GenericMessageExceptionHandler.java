package com.ice.musicmetadata.controller.handler;

import com.ice.musicmetadata.model.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
@ControllerAdvice
public class GenericMessageExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class})
    public final ResponseEntity<ErrorResponse> handleBadRequestException(IllegalArgumentException ex) {
        return errorResponseEntity(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public final @NotNull ResponseEntity<ErrorResponse> resourceNotFoundException(Exception ex) {
        return errorResponseEntity(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(Exception.class)
    public final @NotNull ResponseEntity<ErrorResponse> generalException(Exception ex) {
        var errorMessage = errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, List.of("An error occurred, please try again later or contact support."));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
    }

    @Override
    protected @NotNull ResponseEntity<Object> handleHttpMessageNotReadable(
            @NotNull HttpMessageNotReadableException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode status,
            @NotNull WebRequest request) {
        var errorMessage = errorResponse(HttpStatus.BAD_REQUEST, Objects.requireNonNullElse(ex.getMostSpecificCause(), ex), null);
        return ResponseEntity.status(status).body(errorMessage);
    }

    @ExceptionHandler({ValidationException.class, NumberFormatException.class})
    public final ResponseEntity<ErrorResponse> clientException(Exception ex) {
        if (ex instanceof ConstraintViolationException exc) {
            var constraintViolations = exc.getConstraintViolations();
            var errors = constraintViolations.stream().map(ConstraintViolation::getMessage).toList();
            var errorMessage = errorResponse(HttpStatus.BAD_REQUEST, ex, errors);
            return ResponseEntity.status(errorMessage.getStatus()).body(errorMessage);
        }
        return errorResponseEntity(HttpStatus.BAD_REQUEST, ex);
    }

    @Override
    protected final ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            @NotNull HttpRequestMethodNotSupportedException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode status,
            @NotNull WebRequest request) {
        var errorMessage = errorResponse(HttpStatus.BAD_REQUEST, ex, null);
        return ResponseEntity.status(errorMessage.getStatus()).body(errorMessage);
    }

    @Override
    protected final @NotNull ResponseEntity<Object> handleNoHandlerFoundException(
            @NotNull NoHandlerFoundException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode status,
            @NotNull WebRequest request) {
        var errorMessage = errorResponse(HttpStatus.BAD_REQUEST, ex, null);
        return ResponseEntity.status(errorMessage.getStatus()).body(errorMessage);
    }


    private @NotNull ResponseEntity<Object> _handleBindException(
            @NotNull BindException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode status,
            @NotNull WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            HttpServletResponse response = servletWebRequest.getResponse();
            if (response != null && response.isCommitted()) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Response already committed. Ignoring: " + ex);
                }
                return null;
            }
        }
        var fieldErrors =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(error -> String.format("%s : %s", error.getField(), error.getDefaultMessage()))
                        .toList();
        var globalErrors =
                ex.getBindingResult().getGlobalErrors().stream()
                        .map(
                                error -> String.format("%s : %s", error.getObjectName(), error.getDefaultMessage()))
                        .toList();
        var errors = Stream.of(fieldErrors, globalErrors).flatMap(List::stream).toList();
        var errorMessage = errorResponse(HttpStatus.BAD_REQUEST, ex, errors);
        return ResponseEntity.status(errorMessage.getStatus()).body(errorMessage);
    }


    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        if (ex instanceof BindException) {
            return _handleBindException((BindException) ex, headers, statusCode, request);
        }
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    //HELPER METHODS
    private ResponseEntity<ErrorResponse> errorResponseEntity(ErrorResponse errorMessage) {
        return ResponseEntity.status(errorMessage.getStatus()).body(errorMessage);
    }

    private ResponseEntity<ErrorResponse> errorResponseEntity(HttpStatus status, Exception ex) {
        var errorMessage = new ErrorResponse().status(status.value())
                .message(ex.getMessage())
                .error(status.getReasonPhrase())
                .timestamp(OffsetDateTime.now());
        return errorResponseEntity(errorMessage);
    }


    private ErrorResponse errorResponse(
            HttpStatus status, Throwable ex, List<String> errors) {
        var error = errors == null ? status.getReasonPhrase() : String.join("\n", errors);
        return new ErrorResponse().status(status.value())
                .message(ex.getMessage())
                .error(error)
                .timestamp(OffsetDateTime.now());
    }
}
