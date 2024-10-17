package com.khantech.transactionmanager.scheduller;

import com.khantech.transactionmanager.entity.TransactionStatus;
import com.khantech.transactionmanager.repository.TransactionRepository;
import com.khantech.transactionmanager.service.UserService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class PendingTransactionService {

  private final UserService userService;
  private final TransactionRepository transactionRepository;

  @Transactional
  @Scheduled(fixedDelayString = "${transaction.pending.process.delay}")
  protected void processPendingTransactions() {
    var pendingTransactions = transactionRepository.findByStatus(TransactionStatus.PENDING);
    for (var transaction : pendingTransactions) {
      if (LocalDateTime.now().minusHours(24).isAfter(transaction.getCreatedAt())) {
        try {
          userService.deductBalance(transaction.getUserId(), transaction.getAmount());
          transaction.setStatus(TransactionStatus.APPROVED);
          transactionRepository.save(transaction);
          log.info("Automatically approved pending transaction: {}", transaction.getId());
        } catch (Exception e) {
          transaction.setStatus(TransactionStatus.REJECTED);
          transactionRepository.save(transaction);
          log.error("Failed to process pending transaction: {}", transaction.getId(), e);
        }
      }
    }
  }
}
