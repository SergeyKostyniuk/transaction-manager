package com.khantech.transactionmanager.service.impl;

import com.khantech.transactionmanager.entity.User;
import com.khantech.transactionmanager.exception.InsufficientBalanceException;
import com.khantech.transactionmanager.exception.UserNotFoundException;
import com.khantech.transactionmanager.repository.UserRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @InjectMocks
  private UserServiceImpl userService;
  @Mock
  private UserRepository userRepository;

  private User user;
  private UUID userId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = new User();
    user.setId(userId);
    user.setName("John Doe");
    user.setBalance(BigDecimal.valueOf(1000));
  }

  @Test
  void shouldReturnUser() {
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    User result = userService.getUserById(userId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(userId);
    assertThat(result.getName()).isEqualTo("John Doe");
    assertThat(result.getBalance()).isEqualTo(BigDecimal.valueOf(1000));

    verify(userRepository).findById(userId);
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void shouldThrowUserNotFoundExceptionWhenNonExistingUser() {
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.getUserById(userId))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User not found with ID: " + userId);

    verify(userRepository).findById(userId);
    verifyNoMoreInteractions(userRepository);
  }

  @ParameterizedTest
  @CsvSource({
      "500, true",
      "1500, false"
  })
  void shouldHandleBalanceValidation(BigDecimal amount, boolean isValid) {
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    if (isValid) {
      userService.validateUserBalance(userId, amount);
    } else {
      assertThatThrownBy(() -> userService.validateUserBalance(userId, amount))
          .isInstanceOf(InsufficientBalanceException.class)
          .hasMessageContaining("Insufficient balance for user: " + userId);
    }

    verify(userRepository).findById(userId);
    verifyNoMoreInteractions(userRepository);
  }

  @ParameterizedTest
  @CsvSource({
      "500, 500",
      "1000, 0"
  })
  void shouldDeductAndSaveWhenSufficientBalance(BigDecimal deductAmount, BigDecimal expectedBalance) {
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(user);

    userService.deductBalance(userId, deductAmount);

    assertThat(user.getBalance()).isEqualTo(expectedBalance);

    verify(userRepository).findById(userId);
    verify(userRepository).save(user);
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void shouldThrowInsufficientBalanceExceptionWhenDeductBalanceAndInsufficientBalance() {
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    assertThatThrownBy(() -> userService.deductBalance(userId, BigDecimal.valueOf(1500)))
        .isInstanceOf(InsufficientBalanceException.class)
        .hasMessageContaining("Insufficient balance for user: " + userId);

    verify(userRepository).findById(userId);
    verifyNoMoreInteractions(userRepository);
  }
}
