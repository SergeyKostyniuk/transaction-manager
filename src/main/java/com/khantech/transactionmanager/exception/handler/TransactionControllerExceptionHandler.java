package com.khantech.transactionmanager.exception.handler;

import com.khantech.transactionmanager.controller.TransactionController;
import com.khantech.transactionmanager.exception.InsufficientBalanceException;
import com.khantech.transactionmanager.exception.UserNotFoundException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(assignableTypes = TransactionController.class)
public class TransactionControllerExceptionHandler {

  @ExceptionHandler(BindException.class)
  public ResponseEntity<String> handleBindException(BindException ex) {
    log.error("Received Validation exception when processing request ", ex);

    final var errMsg = ex.getAllErrors().stream().map(objectError -> {
      if (objectError instanceof FieldError fieldError) {
        return "field '" + fieldError.getField() + "' " + fieldError.getDefaultMessage();
      } else {
        return objectError.getDefaultMessage();
      }
    }).collect(Collectors.joining(", "));

    return new ResponseEntity<>(errMsg, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InsufficientBalanceException.class)
  public ResponseEntity<String> handleInsufficientBalanceException(InsufficientBalanceException ex) {
    log.error("Received InsufficientBalanceException exception when processing request ", ex);
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
    log.error("Received UserNotFoundException exception when processing request ", ex);
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneralException(Exception ex) {
    log.error("Received general exception when processing request ", ex);
    return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
  }
}