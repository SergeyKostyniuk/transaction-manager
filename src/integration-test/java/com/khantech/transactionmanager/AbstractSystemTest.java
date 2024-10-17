package com.khantech.transactionmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khantech.transactionmanager.TransactionManagementApplication;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest(classes = TransactionManagementApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@ContextConfiguration(initializers = {PostgresContainerInitializer.class})
public abstract class AbstractSystemTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

}