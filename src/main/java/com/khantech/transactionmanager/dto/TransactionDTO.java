package com.khantech.transactionmanager.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import lombok.NoArgsConstructor;

@Data
public class TransactionDTO {
    private UUID id;
    @NotNull(message = "User ID is required")
    private UUID userId;
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}
