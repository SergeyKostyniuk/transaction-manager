package com.khantech.transactionmanager.service;

import com.khantech.transactionmanager.entity.User;
import java.math.BigDecimal;
import java.util.UUID;

public interface UserService {

  User getUserById(UUID userId);

  void validateUserBalance(UUID userId, BigDecimal amount);

  void deductBalance(UUID userId, BigDecimal amount);

}
