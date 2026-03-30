package com.shirin.jpaspecdynamicsearchengine.book.web.view;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    ModelAndView error404Handler(NoHandlerFoundException exception)
    {
        ModelAndView mav= new ModelAndView();
        mav.setViewName("404");
        mav.addObject("error","Not Found :(");
        mav.addObject("topic","Not_Found_Page");
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllExceptions(Exception ex) {
        ModelAndView mav = new ModelAndView("404");
        mav.addObject("error", ex.getMessage());
        mav.addObject("topic","Error!!");
        return mav;
    }
}
