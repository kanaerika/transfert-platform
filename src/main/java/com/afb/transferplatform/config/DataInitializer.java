package com.afb.transferplatform.config;

import com.afb.transferplatform.entity.Agent;
import com.afb.transferplatform.repository.AgentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Au premier démarrage : crée le compte SUPER ADMIN d'Afriland First Bank.
 * C'est le seul compte avec un mot de passe pré-défini (pour amorcer le système) ;
 * tous les autres comptes sont activés par invitation.
 */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

   @Bean
CommandLineRunner initialiser(AgentRepository agents, PasswordEncoder encoder) {

    return args -> {

        if (agents.findByEmail("admin@afriland.cm").isEmpty()) {

            Agent superAdmin = new Agent();

            superAdmin.setNomComplet("Afriland First Bank");
            superAdmin.setEmail("admin@afriland.cm");
            superAdmin.setRole("SUPER_ADMIN");
            superAdmin.setMotDePasse(encoder.encode("Admin@2026"));
            superAdmin.setAgence("SIEGE");
            superAdmin.setCodeAgent("00");

            agents.save(superAdmin);

            log.info("SUPER ADMIN créé.");

        }

    };
}
}
