package com.afb.transferplatform.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "agents")
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomComplet;

    @Column(nullable = false, unique = true)
    private String telephone;

    @Column(nullable = false, unique = true)
    private String email;

    /** Rôle : Instant Transfert, Financial House, Julie Voyage, Express Union, Caisse */
    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String motDePasse;

    private String codeAgent; // ex: "04"
    private String agence;    // ex: "DEI"

    public Agent() {}

    public Agent(String nomComplet, String telephone, String email, String role,
                 String motDePasse, String codeAgent, String agence) {
        this.nomComplet = nomComplet;
        this.telephone = telephone;
        this.email = email;
        this.role = role;
        this.motDePasse = motDePasse;
        this.codeAgent = codeAgent;
        this.agence = agence;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getCodeAgent() { return codeAgent; }
    public void setCodeAgent(String codeAgent) { this.codeAgent = codeAgent; }
    public String getAgence() { return agence; }
    public void setAgence(String agence) { this.agence = agence; }
}