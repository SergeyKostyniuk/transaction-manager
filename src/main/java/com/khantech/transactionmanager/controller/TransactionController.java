package com.khantech.transactionmanager.controller;

import com.khantech.transactionmanager.dto.TransactionDTO;
import com.khantech.transactionmanager.entity.Transaction;
import com.khantech.transactionmanager.util.ApiConstants;
import com.khantech.transactionmanager.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.API_V1)
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping(ApiConstants.TRANSACTIONS)
    public ResponseEntity<TransactionDTO> createTransaction(@Valid @RequestBody TransactionDTO transactionDto) {
        return ResponseEntity.ok(transactionService.processTransaction(transactionDto));
    }
}
