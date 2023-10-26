package com.gruposuporte.projetosuporte.repository;

import com.gruposuporte.projetosuporte.data.User;
import com.gruposuporte.projetosuporte.data.UserRole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT password FROM User WHERE username = :username")
    Optional<String> findUserPasswordByUsername(String username);

    List<User> findAllByRole(UserRole role);


}
