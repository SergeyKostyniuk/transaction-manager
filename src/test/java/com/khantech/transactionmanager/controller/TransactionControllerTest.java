package com.khantech.transactionmanager.controller;

import com.khantech.transactionmanager.dto.TransactionDTO;
import com.khantech.transactionmanager.service.TransactionService;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerUnitTest {

  @InjectMocks
  private TransactionController transactionController;
  @Mock
  private TransactionService transactionService;

  @Test
  void createTransactionWithValidInputShouldReturnCreatedTransaction() {
    UUID userId = UUID.fromString("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33");
    TransactionDTO inputDto = new TransactionDTO();
    inputDto.setUserId(userId);
    inputDto.setAmount(BigDecimal.valueOf(100));
    TransactionDTO outputDto = new TransactionDTO();
    outputDto.setUserId(userId);
    outputDto.setAmount(BigDecimal.valueOf(100));

    when(transactionService.processTransaction(any(TransactionDTO.class))).thenReturn(outputDto);

    ResponseEntity<TransactionDTO> response = transactionController.createTransaction(inputDto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getUserId()).isEqualTo(userId);
    assertThat(response.getBody().getAmount()).isEqualTo(BigDecimal.valueOf(100));
  }
}
