package com.afb.transferplatform.service;

import com.afb.transferplatform.dto.AuthDtos.*;
import com.afb.transferplatform.entity.Agent;
import com.afb.transferplatform.repository.AgentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** Réservé à l'ADMIN_PARTENAIRE : gestion des agents de SON institution uniquement. */
@Service
public class GestionAgentsService {

    private final AgentRepository agents;
    private final InvitationService invitations;
    private static final String ROLE_AGENT = "AGENT";

    public GestionAgentsService(AgentRepository agents, InvitationService invitations) {
        this.agents = agents;
        this.invitations = invitations;
    }

    public List<UtilisateurResponse> lister(Agent admin) {
        return agents.findByPartenaireIdOrderByNomComplet(partenaireDe(admin)).stream()
                .filter(a -> ROLE_AGENT.equals(a.getRole()))
                .map(this::versReponse)
                .toList();
    }

    /** Crée un agent (sans mot de passe) et lui envoie son invitation. */
    @Transactional
    public UtilisateurResponse creer(AgentRequest req, Agent admin) {
        String email = req.email().trim().toLowerCase();
        if (agents.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Un compte existe déjà avec cet email.");
        }
        Agent a = new Agent();
        a.setNomComplet(req.nomComplet().trim());
        a.setEmail(email);
        a.setRole(ROLE_AGENT);
        a.setPartenaire(admin.getPartenaire());
        a.setAgence(req.agence());
        a.setCodeAgent(req.codeAgent());
        invitations.inviter(a, false);
        agents.save(a);
        return versReponse(a);
    }

    @Transactional
    public UtilisateurResponse modifier(Long id, AgentRequest req, Agent admin) {
        Agent a = chargerDansMonPartenaire(id, admin);
        a.setNomComplet(req.nomComplet().trim());
        a.setAgence(req.agence());
        a.setCodeAgent(req.codeAgent());
        agents.save(a);
        return versReponse(a);
    }

    @Transactional
    public UtilisateurResponse basculerActif(Long id, Agent admin) {
        Agent a = chargerDansMonPartenaire(id, admin);
        a.setActif(!a.isActif());
        agents.save(a);
        return versReponse(a);
    }

    /** Renvoie l'invitation (compte jamais activé). */
    @Transactional
    public MessageResponse reinviter(Long id, Agent admin) {
        Agent a = chargerDansMonPartenaire(id, admin);
        if (a.compteActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce compte est déjà activé.");
        }
        invitations.inviter(a, false);
        agents.save(a);
        return new MessageResponse("Invitation renvoyée à " + a.getEmail() + ".");
    }

    /** Réinitialise le mot de passe : l'agent reçoit un lien pour en choisir un nouveau. */
    @Transactional
    public MessageResponse reinitialiserMotDePasse(Long id, Agent admin) {
        Agent a = chargerDansMonPartenaire(id, admin);
        a.setMotDePasse(null); // le compte repasse en attente d'activation
        invitations.inviter(a, true);
        agents.save(a);
        return new MessageResponse("Lien de réinitialisation envoyé à " + a.getEmail() + ".");
    }

    /** Isolation stricte : un admin ne touche jamais aux agents d'un autre partenaire. */
    private Agent chargerDansMonPartenaire(Long id, Agent admin) {
        Agent a = agents.findById(Objects.requireNonNull(id)).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Agent introuvable."));
        if (a.getPartenaire() == null
            || !a.getPartenaire().getId().equals(partenaireDe(admin))
            || !ROLE_AGENT.equals(a.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Cet utilisateur n'appartient pas à votre institution.");
        }
        return a;
    }

    private Long partenaireDe(Agent admin) {
        if (admin.getPartenaire() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Compte non rattaché à un partenaire.");
        }
        return admin.getPartenaire().getId();
    }

    private UtilisateurResponse versReponse(Agent a) {
        return new UtilisateurResponse(a.getId(), a.getNomComplet(), a.getEmail(), a.getRole(),
                a.getNomPartenaire(), a.getAgence(), a.isActif(),
                a.compteActive() ? "Actif" : "Invitation en attente");
    }
}
