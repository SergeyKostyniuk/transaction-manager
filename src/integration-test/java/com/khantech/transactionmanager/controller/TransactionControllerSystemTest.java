package com.khantech.transactionmanager.controller;

import com.khantech.transactionmanager.AbstractSystemTest;
import com.khantech.transactionmanager.dto.TransactionDTO;
import com.khantech.transactionmanager.entity.Transaction;
import com.khantech.transactionmanager.entity.TransactionStatus;
import com.khantech.transactionmanager.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/test-data/insert_test_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/clear-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TransactionControllerSystemTest extends AbstractSystemTest {

  private static final UUID TEST_USER_ID = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");

  @Autowired
  private TransactionRepository transactionRepository;

  @Value("${transaction.approval-threshold}")
  private BigDecimal approvalThreshold;

  @Test
  void createTransactionWithValidInputShouldCreateTransaction() throws Exception {
    TransactionDTO inputDto = new TransactionDTO();
    inputDto.setUserId(TEST_USER_ID);
    inputDto.setAmount(BigDecimal.valueOf(100));

    mockMvc.perform(post("/api/v1/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.userId").value(TEST_USER_ID.toString()))
        .andExpect(jsonPath("$.amount").value(100));
  }

  @Test
  void createTransactionWithInsufficientBalanceShouldReturnBadRequest() throws Exception {
    TransactionDTO inputDto = new TransactionDTO();
    inputDto.setUserId(TEST_USER_ID);
    inputDto.setAmount(BigDecimal.valueOf(20000));

    mockMvc.perform(post("/api/v1/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Insufficient balance")));
  }

  @Test
  void createTransactionWithNonExistentUserShouldReturnNotFound() throws Exception {
    UUID nonExistentUserId = UUID.randomUUID();
    TransactionDTO inputDto = new TransactionDTO();
    inputDto.setUserId(nonExistentUserId);
    inputDto.setAmount(BigDecimal.valueOf(100));

    mockMvc.perform(post("/api/v1/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isNotFound())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("User not found")));
  }

  @Test
  void createTransactionWithNegativeAmount_shouldThrowInvalidTransactionException() throws Exception {
    TransactionDTO inputDto = new TransactionDTO();
    inputDto.setUserId(TEST_USER_ID);
    inputDto.setAmount(BigDecimal.valueOf(-100));


    mockMvc.perform(post("/api/v1/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("field 'amount' Amount must be positive")));
  }

  @Test
  void createTransactionAboveThreshold_shouldSetStatusToAwaitingApproval() throws Exception {
    BigDecimal amountAboveThreshold = approvalThreshold.add(BigDecimal.ONE);
    TransactionDTO inputDto = new TransactionDTO();
    inputDto.setUserId(TEST_USER_ID);
    inputDto.setAmount(amountAboveThreshold);

    MvcResult result = mockMvc.perform(post("/api/v1/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.userId").value(TEST_USER_ID.toString()))
        .andExpect(jsonPath("$.amount").value(amountAboveThreshold.doubleValue()))
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    TransactionDTO responseDto = objectMapper.readValue(responseBody, TransactionDTO.class);
    UUID transactionId = responseDto.getId();

    Transaction savedTransaction = transactionRepository.findById(transactionId).orElseThrow();
    assertThat(savedTransaction.getStatus()).isEqualTo(TransactionStatus.AWAITING_APPROVAL);
    assertThat(savedTransaction.getAmount()).isEqualByComparingTo(amountAboveThreshold);
    assertThat(savedTransaction.getUserId()).isEqualTo(TEST_USER_ID);
  }
}