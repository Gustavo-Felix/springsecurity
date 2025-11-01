package com.gustavo.springsecurity.repositories;

import com.gustavo.springsecurity.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
