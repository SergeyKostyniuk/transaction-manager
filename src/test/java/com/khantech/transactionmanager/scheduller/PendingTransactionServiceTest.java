package com.khantech.transactionmanager.scheduller;

import com.khantech.transactionmanager.entity.Transaction;
import com.khantech.transactionmanager.entity.TransactionStatus;
import com.khantech.transactionmanager.repository.TransactionRepository;
import com.khantech.transactionmanager.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PendingTransactionServiceTest {

  @InjectMocks
  private PendingTransactionService pendingTransactionService;
  @Mock
  private UserService userService;
  @Mock
  private TransactionRepository transactionRepository;

  private UUID userId;
  private Transaction oldPendingTransaction;
  private Transaction recentPendingTransaction;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();

    oldPendingTransaction = new Transaction();
    oldPendingTransaction.setUserId(userId);
    oldPendingTransaction.setId(UUID.randomUUID());
    oldPendingTransaction.setAmount(BigDecimal.valueOf(100));
    oldPendingTransaction.setStatus(TransactionStatus.PENDING);
    oldPendingTransaction.setCreatedAt(LocalDateTime.now().minusHours(25));

    recentPendingTransaction = new Transaction();
    recentPendingTransaction.setUserId(userId);
    recentPendingTransaction.setId(UUID.randomUUID());
    recentPendingTransaction.setAmount(BigDecimal.valueOf(200));
    recentPendingTransaction.setStatus(TransactionStatus.PENDING);
    recentPendingTransaction.setCreatedAt(LocalDateTime.now().minusHours(23));
  }

  @Test
  void shouldProcessOldTransactionsOnly() {
    when(transactionRepository.findByStatus(TransactionStatus.PENDING))
        .thenReturn(Arrays.asList(oldPendingTransaction, recentPendingTransaction));

    pendingTransactionService.processPendingTransactions();

    verify(userService).deductBalance(oldPendingTransaction.getUserId(), oldPendingTransaction.getAmount());
    verify(transactionRepository).save(oldPendingTransaction);
    verify(userService, never()).deductBalance(recentPendingTransaction.getUserId(),
        recentPendingTransaction.getAmount());
    verify(transactionRepository, never()).save(recentPendingTransaction);

    assertThat(oldPendingTransaction.getStatus()).isEqualTo(TransactionStatus.APPROVED);
    assertThat(recentPendingTransaction.getStatus()).isEqualTo(TransactionStatus.PENDING);

    verifyNoMoreInteractions(userService, transactionRepository);
  }

  @ParameterizedTest
  @CsvSource({
      "100, APPROVED",
      "1000, REJECTED"
  })
  void shouldHandleDeductionOutcome(BigDecimal amount, TransactionStatus expectedStatus) {
    oldPendingTransaction.setAmount(amount);
    when(transactionRepository.findByStatus(TransactionStatus.PENDING))
        .thenReturn(List.of(oldPendingTransaction));

    if (expectedStatus == TransactionStatus.REJECTED) {
      doThrow(new RuntimeException("Deduction failed")).when(userService)
          .deductBalance(oldPendingTransaction.getUserId(), oldPendingTransaction.getAmount());
    }

    pendingTransactionService.processPendingTransactions();

    verify(userService).deductBalance(oldPendingTransaction.getUserId(), oldPendingTransaction.getAmount());
    verify(transactionRepository).save(oldPendingTransaction);

    assertThat(oldPendingTransaction.getStatus()).isEqualTo(expectedStatus);

    verifyNoMoreInteractions(userService, transactionRepository);
  }

  @Test
  void shouldDoNothingWhenNoPendingTransactions() {
    when(transactionRepository.findByStatus(TransactionStatus.PENDING))
        .thenReturn(List.of());

    pendingTransactionService.processPendingTransactions();

    verify(transactionRepository).findByStatus(TransactionStatus.PENDING);
    verifyNoMoreInteractions(userService, transactionRepository);
  }

  @Test
  void shouldProcessAllEligibleWhenMultiplePendingTransactions() {
    Transaction anotherOldTransaction = new Transaction();
    anotherOldTransaction.setId(UUID.randomUUID());
    anotherOldTransaction.setUserId(userId);
    anotherOldTransaction.setAmount(BigDecimal.valueOf(300));
    anotherOldTransaction.setStatus(TransactionStatus.PENDING);
    anotherOldTransaction.setCreatedAt(LocalDateTime.now().minusHours(26));

    when(transactionRepository.findByStatus(TransactionStatus.PENDING))
        .thenReturn(Arrays.asList(oldPendingTransaction, recentPendingTransaction, anotherOldTransaction));

    pendingTransactionService.processPendingTransactions();

    verify(userService).deductBalance(oldPendingTransaction.getUserId(), oldPendingTransaction.getAmount());
    verify(userService).deductBalance(anotherOldTransaction.getUserId(), anotherOldTransaction.getAmount());
    verify(transactionRepository).save(oldPendingTransaction);
    verify(transactionRepository).save(anotherOldTransaction);

    verify(userService, never()).deductBalance(recentPendingTransaction.getUserId(),
        recentPendingTransaction.getAmount());
    verify(transactionRepository, never()).save(recentPendingTransaction);

    assertThat(oldPendingTransaction.getStatus()).isEqualTo(TransactionStatus.APPROVED);
    assertThat(anotherOldTransaction.getStatus()).isEqualTo(TransactionStatus.APPROVED);
    assertThat(recentPendingTransaction.getStatus()).isEqualTo(TransactionStatus.PENDING);

    verifyNoMoreInteractions(userService, transactionRepository);
  }
}
