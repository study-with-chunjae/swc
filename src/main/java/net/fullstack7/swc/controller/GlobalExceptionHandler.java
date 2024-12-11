package net.fullstack7.swc.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.Banner;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {
  @ExceptionHandler({
          org.springframework.web.bind.MethodArgumentNotValidException.class,
          org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
          org.springframework.web.bind.MissingServletRequestParameterException.class,
          org.springframework.http.converter.HttpMessageNotReadableException.class,
          org.springframework.validation.BindException.class,
          jakarta.validation.ConstraintViolationException.class,
          IllegalArgumentException.class,
          NumberFormatException.class,
          org.springframework.expression.spel.SpelEvaluationException.class,
          org.thymeleaf.exceptions.TemplateProcessingException.class,
          org.attoparser.ParseException.class,
          org.thymeleaf.exceptions.TemplateInputException.class,
          jakarta.servlet.ServletException.class,
          RuntimeException.class,
          org.springframework.web.servlet.resource.NoResourceFoundException.class
  })
  public String handleBadRequest(Exception e, Model model) {
    log.error("Bad request error: {}", e.getMessage());

    String errorMessage = "잘못된 요청입니다.";
    if (e instanceof MethodArgumentNotValidException) {
      errorMessage = "입력값이 올바르지 않습니다.";
    } else if (e instanceof IllegalArgumentException) {
      errorMessage = e.getMessage();
    }

    model.addAttribute("errors", errorMessage);
    return "main/error";
  }

  @ExceptionHandler(Exception.class)
  public String handleException(Exception e, Model model) {
    log.error("Unexpected error occurred: ", e);
    model.addAttribute("errors", "처리 중 오류가 발생했습니다.");
    return "main/error";
  }
}
