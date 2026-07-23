package com.afb.transferplatform.service;

import com.afb.transferplatform.dto.AuthDtos.*;
import com.afb.transferplatform.entity.Agent;
import com.afb.transferplatform.entity.Partenaire;
import com.afb.transferplatform.repository.AgentRepository;
import com.afb.transferplatform.repository.PartenaireRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** Réservé au SUPER_ADMIN (Afriland) : gestion des partenaires et de leurs administrateurs. */
@Service
public class SuperAdminService {

    private final PartenaireRepository partenaires;
    private final AgentRepository agents;
    private final InvitationService invitations;
    private static final String ROLE_ADMIN_PARTENAIRE = "ADMIN_PARTENAIRE";

    public SuperAdminService(PartenaireRepository partenaires, AgentRepository agents,
                             InvitationService invitations) {
        this.partenaires = partenaires;
        this.agents = agents;
        this.invitations = invitations;
    }

    public List<PartenaireResponse> lister() {
        return partenaires.findAll().stream().map(this::versReponse).toList();
    }

    /** Crée le partenaire ET son administrateur, puis envoie l'invitation par email. */
    @Transactional
    public PartenaireResponse creer(PartenaireRequest req) {
        String nom = req.nom().trim();
        String email = req.email().trim().toLowerCase();
        if (partenaires.existsByNomIgnoreCase(nom)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce partenaire existe déjà.");
        }
        if (agents.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Un compte existe déjà avec cet email.");
        }
        Partenaire p = partenaires.save(new Partenaire(nom, email));

        Agent admin = new Agent();
        admin.setNomComplet(req.nomAdministrateur().trim());
        admin.setEmail(email);
        admin.setRole(ROLE_ADMIN_PARTENAIRE);
        admin.setPartenaire(p);
        invitations.inviter(admin, false);
        agents.save(admin);
        return versReponse(p);
    }

    @Transactional
    public PartenaireResponse modifier(Long id, ModificationPartenaireRequest req) {
        Partenaire p = charger(id);
        p.setNom(req.nom().trim());
        p.setEmail(req.email().trim().toLowerCase());
        partenaires.save(p);
        return versReponse(p);
    }

    /** Active ou désactive le partenaire (ses utilisateurs ne peuvent plus se connecter). */
    @Transactional
    public PartenaireResponse basculerActif(Long id) {
        Partenaire p = charger(id);
        p.setActif(!p.isActif());
        partenaires.save(p);
        return versReponse(p);
    }

    /** Renvoie l'invitation à l'administrateur du partenaire. */
    @Transactional
    public MessageResponse reinviter(Long id) {
        Partenaire p = charger(id);
        Agent admin = agents.findFirstByPartenaireIdAndRole(p.getId(), ROLE_ADMIN_PARTENAIRE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Administrateur du partenaire introuvable."));
        if (admin.compteActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ce compte est déjà activé.");
        }
        invitations.inviter(admin, false);
        agents.save(admin);
        return new MessageResponse("Invitation renvoyée à " + admin.getEmail() + ".");
    }

    private Partenaire charger(Long id) {
        return partenaires.findById(Objects.requireNonNull(id)).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Partenaire introuvable."));
    }

    private PartenaireResponse versReponse(Partenaire p) {
        String statut = agents.findFirstByPartenaireIdAndRole(p.getId(), ROLE_ADMIN_PARTENAIRE)
                .map(a -> a.compteActive() ? "Administrateur actif" : "Invitation en attente")
                .orElse("Aucun administrateur");
        return new PartenaireResponse(p.getId(), p.getNom(), p.getEmail(), p.isActif(), statut);
    }
}
