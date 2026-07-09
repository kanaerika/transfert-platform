package com.afb.transferplatform.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "transferts", indexes = {
        @Index(name = "idx_transfert_client", columnList = "nomClient"),
        @Index(name = "idx_transfert_date", columnList = "dateTransfert")
})
public class Transfert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomClient;

    private String dateNaissance;
    private String naturePiece;
    private String numeroPiece;

    @Column(nullable = false)
    private long montant;

    @Column(nullable = false)
    private String paysDestination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTransfert statut = StatutTransfert.EXECUTE;

    /** Référence de la plateforme de transfert (ex: PLT-0098-2026) */
    private String reference;

    private String agence;

    /** Canal utilisé : MoneyGram, Small World, ... */
    private String canal;

    @Column(nullable = false)
    private LocalDate dateTransfert;

    /** Cumul Hors CEMAC du mois pour ce client au moment de l'exécution */
    private long cumulMois;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private Agent agent;

    public Transfert() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomClient() { return nomClient; }
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }
    public String getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(String dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getNaturePiece() { return naturePiece; }
    public void setNaturePiece(String naturePiece) { this.naturePiece = naturePiece; }
    public String getNumeroPiece() { return numeroPiece; }
    public void setNumeroPiece(String numeroPiece) { this.numeroPiece = numeroPiece; }
    public long getMontant() { return montant; }
    public void setMontant(long montant) { this.montant = montant; }
    public String getPaysDestination() { return paysDestination; }
    public void setPaysDestination(String paysDestination) { this.paysDestination = paysDestination; }
    public StatutTransfert getStatut() { return statut; }
    public void setStatut(StatutTransfert statut) { this.statut = statut; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getAgence() { return agence; }
    public void setAgence(String agence) { this.agence = agence; }
    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }
    public LocalDate getDateTransfert() { return dateTransfert; }
    public void setDateTransfert(LocalDate dateTransfert) { this.dateTransfert = dateTransfert; }
    public long getCumulMois() { return cumulMois; }
    public void setCumulMois(long cumulMois) { this.cumulMois = cumulMois; }
    public Agent getAgent() { return agent; }
    public void setAgent(Agent agent) { this.agent = agent; }
}