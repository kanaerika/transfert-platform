package com.afb.transferplatform.service;


import com.afb.transferplatform.dto.TransfertDtos.*;
import com.afb.transferplatform.entity.Agent;
import com.afb.transferplatform.entity.CompteurJournalier;
import com.afb.transferplatform.entity.StatutTransfert;
import com.afb.transferplatform.entity.Transfert;
import com.afb.transferplatform.repository.CompteurJournalierRepository;
import com.afb.transferplatform.repository.TransfertRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class TransfertService {

    private final TransfertRepository transfertRepository;
    private final CompteurJournalierRepository compteurRepository;
    private final long plafond;

    private static final DateTimeFormatter FMT_FR =
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);

    public TransfertService(TransfertRepository transfertRepository,
                            CompteurJournalierRepository compteurRepository,
                            @Value("${app.plafond-mensuel:1000000}") long plafond) {
        this.transfertRepository = transfertRepository;
        this.compteurRepository = compteurRepository;
        this.plafond = plafond;
    }

    /** Vérifie si le client peut transférer sans dépasser le plafond mensuel Hors CEMAC. */
    @Transactional
    public VerificationResponse verifier(VerificationRequest req, Agent agent) {
        long cumul = cumulDuMois(req.nomClient());
        long restant = Math.max(0, plafond - cumul);
        boolean autorise = req.montant() <= restant;

        if (!autorise) {
            // Un refus est compté comme "rejeté" dans le bilan journalier de l'agent.
            incrementer(agent, c -> c.setRejetes(c.getRejetes() + 1));
        }

        int pctUtilise = (int) Math.min(100, Math.round(cumul * 100.0 / plafond));
        int pctApres = (int) Math.min(100, Math.round((cumul + req.montant()) * 100.0 / plafond));

        DernierTransfert dernier = transfertRepository
                .findFirstByNomClientIgnoreCaseOrderByIdDesc(req.nomClient().trim())
                .map(t -> new DernierTransfert(t.getNomClient(),
                        t.getDateTransfert().format(FMT_FR), t.getMontant(),
                        t.getStatut().getLibelle()))
                .orElse(null);

        String montantFmt = String.format(Locale.FRENCH, "%,d", req.montant());
        String message = autorise
                ? "Le transfert de " + montantFmt + " FCFA est valide. Vous pouvez exécuter cette opération."
                : "Plafond dépassé — ce client ne peut pas transférer " + montantFmt + " FCFA ce mois-ci.";

        return new VerificationResponse(autorise, message, plafond, cumul,
                req.montant(), restant, pctUtilise, pctApres, dernier);
    }

    /** Enregistre le transfert exécuté (après saisie de la référence plateforme). */
    @Transactional
    public TransfertResponse executer(ExecutionRequest req, Agent agent) {
        long cumul = cumulDuMois(req.nomClient());
        if (req.montant() > plafond - cumul) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Plafond mensuel dépassé : exécution refusée.");
        }

        Transfert t = new Transfert();
        t.setNomClient(req.nomClient().trim());
        t.setDateNaissance(req.dateNaissance().trim());
        t.setNaturePiece(req.naturePiece());
        t.setNumeroPiece(req.numeroPiece().trim());
        t.setMontant(req.montant());
        t.setPaysDestination(req.paysDestination());
        t.setStatut(StatutTransfert.EXECUTE);
        t.setReference(req.reference().trim());
        t.setAgence(agent.getAgence());
        t.setCanal(req.canal());
        t.setDateTransfert(LocalDate.now());
        t.setCumulMois(cumul + req.montant());
        t.setAgent(agent);
        transfertRepository.save(t);

        incrementer(agent, c -> c.setExecutes(c.getExecutes() + 1));
        return TransfertResponse.from(t);
    }

    @Transactional(readOnly = true)
    public List<TransfertResponse> historique(String recherche) {
        return filtrer(transfertRepository.findAllByOrderByIdDesc(), recherche);
    }

    /** Transferts exécutés (annulables). */
    @Transactional(readOnly = true)
    public List<TransfertResponse> annulables(String recherche) {
        return filtrer(transfertRepository.findByStatutOrderByIdDesc(StatutTransfert.EXECUTE), recherche);
    }

    @Transactional
    public TransfertResponse annuler(Long id, Agent agent) {
        Transfert t = transfertRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Transfert introuvable."));
        if (t.getStatut() != StatutTransfert.EXECUTE) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Seul un transfert exécuté peut être annulé.");
        }
        t.setStatut(StatutTransfert.ANNULE);
        transfertRepository.save(t);
        incrementer(agent, c -> c.setAnnules(c.getAnnules() + 1));
        return TransfertResponse.from(t);
    }

    @Transactional(readOnly = true)
    public BilanResponse bilan(Agent agent) {
        LocalDate jour = LocalDate.now();
        CompteurJournalier c = compteurRepository.findByAgentAndJour(agent, jour)
                .orElseGet(() -> new CompteurJournalier(agent, jour));
        int total = c.getExecutes() + c.getRejetes() + c.getAnnules() + c.getNonClotures();
        return new BilanResponse(jour, c.getExecutes(), c.getRejetes(),
                c.getAnnules(), c.getNonClotures(), total);
    }

    // ------------------------------------------------------------------

    private long cumulDuMois(String nomClient) {
        LocalDate maintenant = LocalDate.now();
        LocalDate debut = maintenant.withDayOfMonth(1);
        LocalDate fin = maintenant.withDayOfMonth(maintenant.lengthOfMonth());
        return transfertRepository.cumulMensuel(nomClient.trim(), debut, fin);
    }

    private List<TransfertResponse> filtrer(List<Transfert> liste, String recherche) {
        String q = recherche == null ? "" : recherche.trim().toLowerCase();
        return liste.stream()
                .filter(t -> q.isEmpty()
                        || (t.getReference() != null && t.getReference().toLowerCase().contains(q))
                        || t.getNomClient().toLowerCase().contains(q))
                .map(TransfertResponse::from)
                .toList();
    }

    private void incrementer(Agent agent, java.util.function.Consumer<CompteurJournalier> maj) {
        LocalDate jour = LocalDate.now();
        CompteurJournalier c = compteurRepository.findByAgentAndJour(agent, jour)
                .orElseGet(() -> new CompteurJournalier(agent, jour));
        maj.accept(c);
        compteurRepository.save(c);
    }
}