package com.badri.repository;

import com.badri.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
//    List<User> findByEmailOrPassword(String email, String password);
//    Optional<User> findByMobileNo(String phoneNumber);
//    Optional<User> findByFirstName(String username);
    Boolean existsByPassword(String password);
     Boolean existsByEmail(String email);

}
