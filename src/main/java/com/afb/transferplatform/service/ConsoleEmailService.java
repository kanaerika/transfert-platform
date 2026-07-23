package com.afb.transferplatform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** Mode développement : l'email s'affiche dans les logs du backend. */
@Service
@ConditionalOnProperty(name = "app.email.provider", havingValue = "console", matchIfMissing = true)
public class ConsoleEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailService.class);

    @Override
    public void envoyer(String destinataire, String sujet, String message) {
        log.info("========================================");
        log.info("[EMAIL SIMULÉ] À     : {}", destinataire);
        log.info("[EMAIL SIMULÉ] Sujet : {}", sujet);
        log.info("[EMAIL SIMULÉ] {}", message.replace("\n", " | "));
        log.info("========================================");
    }
}
