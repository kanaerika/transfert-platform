package com.afb.transferplatform.repository;

import com.afb.transferplatform.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Long> {
    Optional<Agent> findByTelephone(String telephone);
    boolean existsByTelephone(String telephone);
    boolean existsByEmail(String email);
}