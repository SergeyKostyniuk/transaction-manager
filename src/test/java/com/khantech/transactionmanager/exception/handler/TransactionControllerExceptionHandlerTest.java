package com.khantech.transactionmanager.exception.handler;

import com.khantech.transactionmanager.exception.InsufficientBalanceException;
import com.khantech.transactionmanager.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerExceptionHandlerTest {

  @InjectMocks
  private TransactionControllerExceptionHandler exceptionHandler;

  @Mock
  private BindException mockBindException;

  @Test
  void handleBindExceptionShouldReturnBadRequest() {
    FieldError fieldError = new FieldError("objectName", "fieldName", "defaultMessage");
    when(mockBindException.getAllErrors()).thenReturn(java.util.Collections.singletonList(fieldError));

    ResponseEntity<String> response = exceptionHandler.handleBindException(mockBindException);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("field 'fieldName' defaultMessage", response.getBody());
  }

  @Test
  void handleInsufficientBalanceExceptionShouldReturnBadRequest() {
    String errorMessage = "Insufficient balance";
    InsufficientBalanceException ex = new InsufficientBalanceException(errorMessage);

    ResponseEntity<String> response = exceptionHandler.handleInsufficientBalanceException(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(errorMessage, response.getBody());
  }

  @Test
  void handleUserNotFoundExceptionShouldReturnNotFound() {
    String errorMessage = "User not found";
    UserNotFoundException ex = new UserNotFoundException(errorMessage);

    ResponseEntity<String> response = exceptionHandler.handleUserNotFoundException(ex);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(errorMessage, response.getBody());
  }

  @Test
  void handleGeneralExceptionShouldReturnInternalServerError() {
    Exception ex = new RuntimeException("Some unexpected error");

    ResponseEntity<String> response = exceptionHandler.handleGeneralException(ex);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("An unexpected error occurred", response.getBody());
  }
}
