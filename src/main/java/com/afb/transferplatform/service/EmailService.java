package com.afb.transferplatform.service;

/** Abstraction d'envoi d'email. Implémentation dev : ConsoleEmailService (logs). */
public interface EmailService {
    void envoyer(String destinataire, String sujet, String message);
}
