package com.afb.transferplatform.service;

import com.afb.transferplatform.entity.Partenaire;
import com.afb.transferplatform.entity.Agent;
import com.afb.transferplatform.repository.PartenaireRepository;
import com.afb.transferplatform.repository.AgentRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class PartenaireService {

    private final PartenaireRepository repository;
    private final AgentRepository agentRepository;
    private final EmailService emailService;

    public PartenaireService(PartenaireRepository repository,
                             AgentRepository agentRepository,
                             EmailService emailService) {
        this.repository = repository;
        this.agentRepository = agentRepository;
        this.emailService = emailService;
    }

    public List<Partenaire> getAll() {
        return repository.findAll();
    }

    public Partenaire create(Partenaire partenaire) {
        if (repository.existsByNomIgnoreCase(partenaire.getNom())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce partenaire existe déjà.");
        }
        partenaire.setActif(true);
        return repository.save(partenaire);
    }

    public Partenaire update(Long id, Partenaire data) {
        Partenaire p = repository.findById(Objects.requireNonNull(id))
                .orElseThrow();
        p.setNom(data.getNom());
        p.setEmail(data.getEmail());
        return repository.save(p);
    }

    public void supprimer(Long id) {
        repository.deleteById(Objects.requireNonNull(id));
    }

    public Partenaire activer(Long id) {
        Partenaire p = repository.findById(Objects.requireNonNull(id))
                .orElseThrow();
        p.setActif(true);
        return repository.save(p);
    }

    public Partenaire desactiver(Long id) {
        Partenaire p = repository.findById(Objects.requireNonNull(id))
                .orElseThrow();
        p.setActif(false);
        return repository.save(p);
    }

    public void envoyerInvitation(Long partenaireId) {
        Partenaire partenaire = repository.findById(Objects.requireNonNull(partenaireId))
                .orElseThrow();

        Agent agent = new Agent();
        agent.setNomComplet(partenaire.getNom() + " Admin");
        agent.setEmail(partenaire.getEmail());
        agent.setRole("ADMIN_PARTENAIRE");
        agent.setActif(true);
        agent.setPartenaire(partenaire);

        String token = UUID.randomUUID().toString();
        agent.setTokenInvitation(token);
        agent.setTokenExpiration(Instant.now().plus(2, ChronoUnit.DAYS));

        agentRepository.save(agent);

        String sujet = "Invitation - Création de compte";

String message =
        "Bonjour,\n\n" +
        "Vous avez été invité en tant qu'administrateur pour le partenaire "
        + partenaire.getNom()
        + ".\n\n"
        + "Utilisez ce lien pour activer votre compte : "
        + "https://example.com/invite/" + token;

emailService.envoyer(agent.getEmail(), sujet, message);
    }

    public Partenaire getById(Long id) {
        return repository.findById(Objects.requireNonNull(id))
                .orElseThrow();
    }
}
