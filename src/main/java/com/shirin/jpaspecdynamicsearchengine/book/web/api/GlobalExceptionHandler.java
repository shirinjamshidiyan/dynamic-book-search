package com.shirin.jpaspecdynamicsearchengine.book.web.api;

import com.shirin.jpaspecdynamicsearchengine.book.web.api.error.ApiError;
import com.shirin.jpaspecdynamicsearchengine.book.web.api.error.FieldErrorDTO;
import com.shirin.jpaspecdynamicsearchengine.book.web.api.error.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice(basePackages = "com.shirin.jpaspecdynamicsearchengine.book.web.api")
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(
            ValidationException ex,
            HttpServletRequest request) {

        return new ApiError(
                LocalDateTime.now(),
                400,
                "Bad Request",
                "Validation failed",
                request.getRequestURI(),
                ex.getFieldErrors()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<FieldErrorDTO> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldErrorDto)
                .toList();

        return new ApiError(
                LocalDateTime.now(),
                400,
                "Bad Request",
                "Validation failed",
                request.getRequestURI(),
                fieldErrors
        );
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return new ApiError(
                LocalDateTime.now(),
                400,
                "Bad Request",
                 "Malformed JSON request",
                request.getRequestURI(),
                null
        );
    }
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpServletRequest request) {

        return new ApiError(
                LocalDateTime.now(),
                404,
                "Not Found",
                "Resource not found",
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleGeneralException(
            Exception ex,
            HttpServletRequest request) {

        return new ApiError(
                LocalDateTime.now(),
                500,
                "Internal Server Error",
                "Unexpected error occurred",
                request.getRequestURI(),
                null
        );
    }

    private FieldErrorDTO toFieldErrorDto(FieldError error) {
        return new FieldErrorDTO(error.getField(), error.getDefaultMessage());
    }

}
