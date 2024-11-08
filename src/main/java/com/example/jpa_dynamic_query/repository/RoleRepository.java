package com.example.jpa_dynamic_query.repository;

import com.example.jpa_dynamic_query.entity.Role;
import com.example.jpa_dynamic_query.enums.EnumRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(EnumRole role);
}
