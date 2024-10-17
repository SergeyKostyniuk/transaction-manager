package com.khantech.transactionmanager.repository;

import com.khantech.transactionmanager.entity.Transaction;
import com.khantech.transactionmanager.entity.TransactionStatus;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByStatus(TransactionStatus status);

}