package com.khantech.transactionmanager.repository;

import com.khantech.transactionmanager.AbstractSystemTest;
import com.khantech.transactionmanager.entity.Transaction;
import com.khantech.transactionmanager.entity.TransactionStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/test-data/insert_test_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/insert_test_transactions.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/clear-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TransactionRepositorySystemTest extends AbstractSystemTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @ParameterizedTest
    @EnumSource(TransactionStatus.class)
    void shouldReturnCorrectTransactions(TransactionStatus status) {
        List<Transaction> transactions = transactionRepository.findByStatus(status);
        
        assertThat(transactions).isNotEmpty()
                                .allMatch(t -> t.getStatus() == status);
        
        int expectedCount = getExpectedCount(status);
        assertThat(transactions).hasSize(expectedCount);
    }

    private int getExpectedCount(TransactionStatus status) {
        return switch (status) {
            case PENDING -> 2;
            case APPROVED, REJECTED, AWAITING_APPROVAL -> 1;
        };
    }
}
