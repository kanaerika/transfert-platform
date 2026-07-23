package com.afb.transferplatform.service;

import com.afb.transferplatform.entity.Agent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.afb.transferplatform.repository.AgentRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/** Génère les jetons d'invitation et envoie les emails d'activation. */
@Service
public class InvitationService {

    private static final Duration VALIDITE = Duration.ofHours(72);

    private final EmailService emailService;
    private final String urlFrontend;
    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;
    public InvitationService(
        EmailService emailService,
        AgentRepository agentRepository,
        PasswordEncoder passwordEncoder,
        @Value("${app.frontend-url:http://localhost:4200}") String urlFrontend) {

    this.emailService = emailService;
    this.agentRepository = agentRepository;
    this.passwordEncoder = passwordEncoder;
    this.urlFrontend = urlFrontend;
}

    /** (Ré)génère le jeton du compte et envoie le lien d'activation par email. */
    public void inviter(Agent agent, boolean reinitialisation) {
        agent.setTokenInvitation(UUID.randomUUID().toString());
        agent.setTokenExpiration(Instant.now().plus(VALIDITE));
        String lien = urlFrontend + "/activation?token=" + agent.getTokenInvitation();
        String sujet = reinitialisation
                ? "Réinitialisation de votre mot de passe — Plateforme Afriland"
                : "Activez votre compte — Plateforme de transferts Afriland First Bank";
        String message = "Bonjour " + agent.getNomComplet() + ",\n\n"
                + (reinitialisation
                    ? "Une réinitialisation de votre mot de passe a été demandée."
                    : "Un compte a été créé pour vous sur la plateforme de suivi des transferts internationaux ("
                      + agent.getNomPartenaire() + ").")
                + "\nCliquez sur ce lien pour définir votre mot de passe (valide 72 h) :\n"
                + lien + "\n\nAfriland First Bank";
        emailService.envoyer(agent.getEmail(), sujet, message);
    }

    public Agent verifierToken(String token) {

    return agentRepository.findByTokenInvitation(token)
            .orElseThrow(() -> new RuntimeException("Invitation invalide."));
}

    public void activerCompte(String token, String motDePasse) {

    Agent agent = verifierToken(token);

    if (agent.getTokenExpiration().isBefore(Instant.now())) {
        throw new RuntimeException("Le lien d'invitation a expiré.");
    }

    agent.setMotDePasse(passwordEncoder.encode(motDePasse));

    agent.setInvitationAcceptee(true);

    agent.setTokenInvitation(null);

    agent.setTokenExpiration(null);

    agentRepository.save(agent);
}
}
