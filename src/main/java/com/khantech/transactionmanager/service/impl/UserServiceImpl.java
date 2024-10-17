package com.khantech.transactionmanager.service.impl;

import com.khantech.transactionmanager.entity.User;
import com.khantech.transactionmanager.exception.InsufficientBalanceException;
import com.khantech.transactionmanager.exception.UserNotFoundException;
import com.khantech.transactionmanager.repository.UserRepository;
import com.khantech.transactionmanager.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final int ZERO = 0;

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public void validateUserBalance(UUID userId, BigDecimal amount) {
        var user = getUserById(userId);
        if (user.getBalance().compareTo(amount) < ZERO) {
            throw new InsufficientBalanceException("Insufficient balance for user: " + userId);
        }
    }

    @Override
    @Transactional
    public void deductBalance(UUID userId, BigDecimal amount) {
        var user = getUserById(userId);
        if (user.getBalance().compareTo(amount) < ZERO) {
            throw new InsufficientBalanceException("Insufficient balance for user: " + userId);
        }
        user.setBalance(user.getBalance().subtract(amount));

        userRepository.save(user);
    }
}
