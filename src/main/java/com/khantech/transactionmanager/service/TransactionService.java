package com.khantech.transactionmanager.service;

import com.khantech.transactionmanager.dto.TransactionDTO;
import com.khantech.transactionmanager.entity.Transaction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

public interface TransactionService {

  TransactionDTO processTransaction(TransactionDTO transaction);

}
