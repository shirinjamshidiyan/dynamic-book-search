package com.shirin.jpaspecdynamicsearchengine.book.web.view;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice(basePackageClasses = BookSearchViewController.class)
@RequiredArgsConstructor
public class ViewExceptionHandler {

  private final MessageSource messageSource;

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ModelAndView handleAllExceptions(Exception ex, HttpServletRequest request) {
    ModelAndView mav = new ModelAndView("error");
    mav.addObject("error", "Internal Server Error");
    mav.addObject("status", 500);
    mav.addObject("message",
            messageSource.getMessage(
                    "error.internal",
                    null,
                    LocaleContextHolder.getLocale()));
    mav.addObject("path", request.getRequestURI());
    return mav;
  }
}
