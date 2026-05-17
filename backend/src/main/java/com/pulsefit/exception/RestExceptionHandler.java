package com.pulsefit.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  ProblemDetail handleIllegalArgument(IllegalArgumentException exception, HttpServletRequest request) {
    return problem(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ProblemDetail handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
    return problem(HttpStatus.BAD_REQUEST, exception.getBindingResult().getAllErrors().getFirst().getDefaultMessage(), request.getRequestURI());
  }

  @ExceptionHandler(AccessDeniedException.class)
  ProblemDetail handleAccessDenied(AccessDeniedException exception, HttpServletRequest request) {
    return problem(HttpStatus.FORBIDDEN, "You do not have access to this resource.", request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  ProblemDetail handleGeneric(Exception exception, HttpServletRequest request) {
    return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error.", request.getRequestURI());
  }

  private ProblemDetail problem(HttpStatusCode status, String detail, String path) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
    problemDetail.setTitle(status.toString());
    problemDetail.setType(URI.create("https://pulsefit.local/problems/" + status.value()));
    problemDetail.setProperty("path", path);
    return problemDetail;
  }
}
