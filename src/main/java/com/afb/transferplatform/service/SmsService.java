package com.afb.transferplatform.service;

/**
 * Abstraction de l'envoi de SMS.
 * Implémentation par défaut : ConsoleSmsService (affiche le SMS dans les logs, pour le dev).
 * En production, remplacer par une implémentation Twilio / Vonage / Orange SMS API, etc.
 */
public interface SmsService {

    /**
     * Envoie un SMS.
     * @param telephone numéro du destinataire (ex: +237690000000)
     * @param message   contenu du SMS
     */
    void envoyer(String telephone, String message);
}
