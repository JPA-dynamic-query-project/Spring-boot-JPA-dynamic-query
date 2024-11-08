package com.example.jpa_dynamic_query.service;

import com.example.jpa_dynamic_query.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserService extends JpaRepository<User, Integer> {
}
