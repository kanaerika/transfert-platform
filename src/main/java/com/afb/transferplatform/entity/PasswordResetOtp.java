package com.afb.transferplatform.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Code OTP envoyé par SMS pour la réinitialisation du mot de passe.
 * Le code est stocké haché (BCrypt) — jamais en clair.
 */
@Entity
@Table(name = "password_reset_otps")
public class PasswordResetOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String telephone;

    /** Hash BCrypt du code OTP à 6 chiffres */
    @Column(nullable = false)
    private String codeHash;

    @Column(nullable = false)
    private Instant expiration;

    /** Nombre de tentatives de vérification échouées */
    @Column(nullable = false)
    private int tentatives = 0;

    /** true une fois l'OTP vérifié avec succès (étape verify-otp) */
    @Column(nullable = false)
    private boolean verifie = false;

    /** true une fois le mot de passe effectivement réinitialisé (OTP consommé) */
    @Column(nullable = false)
    private boolean utilise = false;

    public PasswordResetOtp() {}

    public PasswordResetOtp(String telephone, String codeHash, Instant expiration) {
        this.telephone = telephone;
        this.codeHash = codeHash;
        this.expiration = expiration;
    }

    public boolean estExpire() {
        return Instant.now().isAfter(expiration);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getCodeHash() { return codeHash; }
    public void setCodeHash(String codeHash) { this.codeHash = codeHash; }
    public Instant getExpiration() { return expiration; }
    public void setExpiration(Instant expiration) { this.expiration = expiration; }
    public int getTentatives() { return tentatives; }
    public void setTentatives(int tentatives) { this.tentatives = tentatives; }
    public boolean isVerifie() { return verifie; }
    public void setVerifie(boolean verifie) { this.verifie = verifie; }
    public boolean isUtilise() { return utilise; }
    public void setUtilise(boolean utilise) { this.utilise = utilise; }
}
