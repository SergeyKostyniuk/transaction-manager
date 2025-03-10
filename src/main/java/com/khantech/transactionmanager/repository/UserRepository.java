package com.khantech.transactionmanager.repository;

import com.khantech.transactionmanager.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
}
