package com.example.policee.dao.repository;

import com.example.policee.dao.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
   // List<UserEntity> findByEmailIs(String email);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
