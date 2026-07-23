package com.afb.transferplatform.repository;

import com.afb.transferplatform.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Long> {
    Optional<Agent> findByEmail(String email);
    Optional<Agent> findByTokenInvitation(String token);
    boolean existsByEmailIgnoreCase(String email);
    List<Agent> findByPartenaireIdOrderByNomComplet(Long partenaireId);
    Optional<Agent> findFirstByPartenaireIdAndRole(Long partenaireId, String role);
}
