package com.khantech.transactionmanager.service.impl;

import com.khantech.transactionmanager.config.TransactionConfig;
import com.khantech.transactionmanager.dto.TransactionDTO;
import com.khantech.transactionmanager.entity.Transaction;
import com.khantech.transactionmanager.entity.TransactionStatus;
import com.khantech.transactionmanager.mapper.TransactionMapper;
import com.khantech.transactionmanager.repository.TransactionRepository;
import com.khantech.transactionmanager.service.TransactionService;
import com.khantech.transactionmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

  public static final int ZERO = 0;

  private final UserService userService;
  private final TransactionConfig transactionConfig;
  private final TransactionMapper transactionMapper;
  private final TransactionRepository transactionRepository;

  @Override
  @Transactional
  public TransactionDTO processTransaction(TransactionDTO transactionDTO) {
    var transaction = transactionMapper.toEntity(transactionDTO);

    userService.validateUserBalance(transaction.getUserId(), transaction.getAmount());

    if (transaction.getAmount().compareTo(transactionConfig.getApprovalThreshold()) > ZERO) {
      transaction.setStatus(TransactionStatus.AWAITING_APPROVAL);
      log.info("Transaction is awaiting approval");
    } else {
      approveTransaction(transaction);
    }

    var savedTransaction = transactionRepository.save(transaction);

    return transactionMapper.toDto(savedTransaction);
  }

  private void approveTransaction(Transaction transaction) {
    userService.deductBalance(transaction.getUserId(), transaction.getAmount());

    transaction.setStatus(TransactionStatus.APPROVED);

    log.info("Transaction approved for user {}", transaction.getUserId());
  }
}
