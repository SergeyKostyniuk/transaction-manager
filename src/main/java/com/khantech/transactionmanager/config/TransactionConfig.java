package com.khantech.transactionmanager.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import org.springframework.validation.annotation.Validated;

@Setter
@Getter
@Validated
@ConfigurationProperties(prefix = "transaction")
public class TransactionConfig {
    @NotNull
    private long pendingProcessDelay;
    @NotNull
    private BigDecimal approvalThreshold;
}
