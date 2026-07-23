package com.afb.transferplatform.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Utilisateur de la plateforme. Trois rôles :
 *  - SUPER_ADMIN      : Afriland First Bank (gère les partenaires, voit tout)
 *  - ADMIN_PARTENAIRE : administrateur d'un partenaire (gère ses agents)
 *  - AGENT            : opérateur (crée et suit les transferts de son partenaire)
 *
 * Tous les comptes (sauf le Super Admin initial) sont activés PAR INVITATION :
 * créés sans mot de passe, l'utilisateur le définit lui-même via un lien sécurisé.
 */
@Entity
@Table(name = "agents")
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomComplet;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String role = "AGENT";
    
    
    /** null pour le SUPER_ADMIN */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "partenaire_id")
    private Partenaire partenaire;

    /** null tant que le compte n'a pas été activé par invitation */
    private String motDePasse;

    /** Activation/désactivation administrative du compte */
    @Column(nullable = false)
    private boolean actif = true;

    @Column(nullable = false)
    private boolean invitationAcceptee = false;

    /** Jeton d'invitation (activation du compte / réinitialisation) */
    private String tokenInvitation;
    private Instant tokenExpiration;

    private String codeAgent;
    private String agence;


    public boolean isInvitationAcceptee() {
    return invitationAcceptee;
}

public void setInvitationAcceptee(boolean invitationAcceptee) {
    this.invitationAcceptee = invitationAcceptee;
}
    public boolean compteActive() { return motDePasse != null; }
    public boolean estSuperAdmin() { return "SUPER_ADMIN".equals(role); }
    public boolean estAdminPartenaire() { return "ADMIN_PARTENAIRE".equals(role); }
    public String getNomPartenaire() { return partenaire != null ? partenaire.getNom() : "Afriland First Bank"; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Partenaire getPartenaire() { return partenaire; }
    public void setPartenaire(Partenaire partenaire) { this.partenaire = partenaire; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public String getTokenInvitation() { return tokenInvitation; }
    public void setTokenInvitation(String tokenInvitation) { this.tokenInvitation = tokenInvitation; }
    public Instant getTokenExpiration() { return tokenExpiration; }
    public void setTokenExpiration(Instant tokenExpiration) { this.tokenExpiration = tokenExpiration; }
    public String getCodeAgent() { return codeAgent; }
    public void setCodeAgent(String codeAgent) { this.codeAgent = codeAgent; }
    public String getAgence() { return agence; }
    public void setAgence(String agence) { this.agence = agence; }
}
