package com.afb.transferplatform.service;

import com.afb.transferplatform.dto.AuthDtos.*;
import com.afb.transferplatform.entity.Agent;
import com.afb.transferplatform.repository.AgentRepository;
import com.afb.transferplatform.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

/** Connexion par email + mot de passe, activation des comptes par invitation. */
@Service
public class AuthService {

    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AgentRepository agentRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.agentRepository = agentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        Agent agent = agentRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(this::identifiantsInvalides);
        if (!agent.compteActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Compte non activé. Consultez votre email d'invitation ou demandez-en un nouveau.");
        }
        if (!agent.isActif()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Compte désactivé. Contactez votre administrateur.");
        }
        if (agent.getPartenaire() != null && !agent.getPartenaire().isActif()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Le partenaire " + agent.getPartenaire().getNom() + " est désactivé.");
        }
        if (!passwordEncoder.matches(request.motDePasse(), agent.getMotDePasse())) {
            throw identifiantsInvalides();
        }
        return new AuthResponse(
                jwtService.generer(agent.getId()),
                agent.getId(), agent.getNomComplet(), agent.getCodeAgent(),
                agent.getRole(), agent.getNomPartenaire());
    }

    /** Activation du compte via le lien d'invitation : l'utilisateur choisit son mot de passe. */
    @Transactional
    public MessageResponse activer(ActivationRequest request) {
        if (!request.motDePasse().equals(request.confirmationMotDePasse())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Les mots de passe ne correspondent pas.");
        }
        Agent agent = agentRepository.findByTokenInvitation(request.token())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Lien d'activation invalide. Demandez une nouvelle invitation."));
        if (agent.getTokenExpiration() == null || Instant.now().isAfter(agent.getTokenExpiration())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Lien d'activation expiré. Demandez une nouvelle invitation.");
        }
        agent.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        agent.setTokenInvitation(null);
        agent.setTokenExpiration(null);
        agentRepository.save(agent);
        return new MessageResponse("Compte activé avec succès. Vous pouvez vous connecter.");
    }

    private ResponseStatusException identifiantsInvalides() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Email ou mot de passe incorrect.");
    }
}
