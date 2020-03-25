package com.twitter.polls.repository;

import com.twitter.polls.model.Role;
import com.twitter.polls.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
