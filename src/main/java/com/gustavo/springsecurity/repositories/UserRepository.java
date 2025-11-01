package com.gustavo.springsecurity.repositories;

import com.gustavo.springsecurity.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
