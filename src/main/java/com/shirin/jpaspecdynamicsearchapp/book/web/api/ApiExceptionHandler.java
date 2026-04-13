package com.shirin.jpaspecdynamicsearchapp.book.web.api;

import com.shirin.jpaspecdynamicsearchapp.book.web.api.error.ApiError;
import com.shirin.jpaspecdynamicsearchapp.book.web.api.error.FieldErrorDTO;
import com.shirin.jpaspecdynamicsearchapp.book.web.api.error.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = BookSearchRestController.class)
@RequiredArgsConstructor
public class ApiExceptionHandler {
  private final MessageSource messageSource;

  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleValidationException(ValidationException ex, HttpServletRequest request) {

    return new ApiError(
        LocalDateTime.now(),
        400,
        "Bad Request",
        messageSource.getMessage(
            "error.validation", null, "Validation Failed", LocaleContextHolder.getLocale()),
        request.getRequestURI(),
        ex.getFieldErrors());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    List<FieldErrorDTO> fieldErrors =
        ex.getBindingResult().getFieldErrors().stream().map(this::toFieldErrorDto).toList();

    return new ApiError(
        LocalDateTime.now(),
        400,
        "Bad Request",
        messageSource.getMessage(
            "error.validation", null, "Validation Failed", LocaleContextHolder.getLocale()),
        request.getRequestURI(),
        fieldErrors);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex, HttpServletRequest request) {

    return new ApiError(
        LocalDateTime.now(),
        400,
        "Bad Request",
        messageSource.getMessage(
            "error.malformedJson", null, "Malformed JSON Request", LocaleContextHolder.getLocale()),
        request.getRequestURI(),
        null);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiError handleGeneralException(Exception ex, HttpServletRequest request) {

    return new ApiError(
        LocalDateTime.now(),
        500,
        "Internal Server Error",
        messageSource.getMessage(
            "error.internal", null, "Unexpected Error Occurred", LocaleContextHolder.getLocale()),
        request.getRequestURI(),
        null);
  }

  private FieldErrorDTO toFieldErrorDto(FieldError error) {
    return new FieldErrorDTO(error.getField(), error.getDefaultMessage());
  }
}
