package com.khantech.transactionmanager.service.impl;

import com.khantech.transactionmanager.config.TransactionConfig;
import com.khantech.transactionmanager.dto.TransactionDTO;
import com.khantech.transactionmanager.entity.Transaction;
import com.khantech.transactionmanager.entity.TransactionStatus;
import com.khantech.transactionmanager.mapper.TransactionMapper;
import com.khantech.transactionmanager.repository.TransactionRepository;
import com.khantech.transactionmanager.service.UserService;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

  @InjectMocks
  private TransactionServiceImpl transactionService;
  @Mock
  private UserService userService;
  @Mock
  private TransactionConfig transactionConfig;
  @Mock
  private TransactionMapper transactionMapper;
  @Mock
  private TransactionRepository transactionRepository;

  private UUID userId;
  private Transaction transaction;
  private TransactionDTO transactionDTO;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    transactionDTO = new TransactionDTO();
    transactionDTO.setUserId(userId);
    transactionDTO.setAmount(BigDecimal.valueOf(100));

    transaction = new Transaction();
    transaction.setUserId(userId);
    transaction.setId(UUID.randomUUID());
    transaction.setAmount(BigDecimal.valueOf(100));
    transaction.setStatus(TransactionStatus.PENDING);
  }

  @ParameterizedTest
  @CsvSource({
      "100, 1000, APPROVED",
      "1500, 1000, AWAITING_APPROVAL"
  })
  void processTransaction_shouldHandleTransactionBasedOnThreshold(BigDecimal transactionAmount, BigDecimal threshold,
      TransactionStatus expectedStatus) {
    transaction.setAmount(transactionAmount);
    transactionDTO.setAmount(transactionAmount);

    when(transactionMapper.toEntity(any(TransactionDTO.class))).thenReturn(transaction);
    when(transactionConfig.getApprovalThreshold()).thenReturn(threshold);
    when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
    when(transactionMapper.toDto(any(Transaction.class))).thenReturn(transactionDTO);

    TransactionDTO result = transactionService.processTransaction(transactionDTO);

    assertThat(result).isNotNull();
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getAmount()).isEqualTo(transactionAmount);

    verify(transactionMapper).toEntity(transactionDTO);
    verify(transactionConfig).getApprovalThreshold();
    verify(userService).validateUserBalance(userId, transactionAmount);
    verify(transactionRepository).save(transaction);
    verify(transactionMapper).toDto(transaction);

    if (expectedStatus == TransactionStatus.APPROVED) {
      verify(userService).deductBalance(userId, transactionAmount);
    } else {
      verify(userService, never()).deductBalance(any(), any());
    }

    assertThat(transaction.getStatus()).isEqualTo(expectedStatus);
    verifyNoMoreInteractions(userService, transactionConfig, transactionMapper, transactionRepository);
  }
}
