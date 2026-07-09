package com.afb.transferplatform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Implémentation de développement : affiche le SMS dans la console/logs.
 * Active par défaut (app.sms.provider=console ou propriété absente).
 *
 * Pour la production, créer une implémentation TwilioSmsService (voir README)
 * et définir app.sms.provider=twilio dans application.yml.
 */
@Service
@ConditionalOnProperty(name = "app.sms.provider", havingValue = "console", matchIfMissing = true)
public class ConsoleSmsService implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleSmsService.class);

    @Override
    public void envoyer(String telephone, String message) {
        log.info("========================================");
        log.info("[SMS SIMULÉ] Destinataire : {}", telephone);
        log.info("[SMS SIMULÉ] Message      : {}", message);
        log.info("========================================");
    }
}
