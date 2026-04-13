package com.shirin.jpaspecdynamicsearchapp.book.web.error;

import com.shirin.jpaspecdynamicsearchapp.book.web.api.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalNotFoundExceptionHandler {

  private final MessageSource messageSource;

  @ExceptionHandler(NoHandlerFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Object handleNoHandlerFoundException(
      NoHandlerFoundException ex, HttpServletRequest request) {

    String path = request.getRequestURI();

    // API → JSON
    if (path.startsWith("/api/")) {
      return new ApiError(
          LocalDateTime.now(),
          404,
          "Not Found",
          messageSource.getMessage(
              "error.notFound", null, "Resource Not Found", LocaleContextHolder.getLocale()),
          path,
          null);
    }

    // VIEW → HTML
    ModelAndView mav = new ModelAndView("error");
    mav.addObject("error", "Not Found");
    mav.addObject("status", 404);
    mav.addObject(
        "message",
        messageSource.getMessage("error.notFound", null, LocaleContextHolder.getLocale()));
    mav.addObject("path", path);

    return mav;
  }
}
