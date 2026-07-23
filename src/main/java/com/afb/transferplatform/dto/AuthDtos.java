package com.afb.transferplatform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    // ---- Connexion ----
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String motDePasse) {}

    public record AuthResponse(
            String token, Long agentId, String nomComplet,
            String codeAgent, String role, String partenaireNom) {}

    // ---- Activation de compte par invitation ----
    public record ActivationRequest(
            @NotBlank String token,
            @NotBlank @Size(min = 6) String motDePasse,
            @NotBlank String confirmationMotDePasse) {}

    // ---- Super Admin : partenaires ----
    public record PartenaireRequest(
            @NotBlank String nom,
            @NotBlank @Email String email,
            @NotBlank String nomAdministrateur) {}

    public record ModificationPartenaireRequest(@NotBlank String nom, @NotBlank @Email String email) {}

    public record PartenaireResponse(
            Long id, String nom, String email, boolean actif,
            String statutAdministrateur) {}

    // ---- Admin partenaire : agents ----
    public record AgentRequest(
            @NotBlank String nomComplet,
            @NotBlank @Email String email,
            String agence,
            String codeAgent) {}

    public record UtilisateurResponse(
            Long id, String nomComplet, String email, String role,
            String partenaireNom, String agence, boolean actif, String statut) {}

    // ---- Mot de passe oublié (OTP email) ----
    public record ForgotPasswordRequest(@NotBlank @Email String email) {}
    public record VerifyOtpRequest(@NotBlank @Email String email, @NotBlank String code) {}
    public record ResetPasswordRequest(
            @NotBlank @Email String email, @NotBlank String code,
            @NotBlank String nouveauMotDePasse, @NotBlank String confirmationMotDePasse) {}

    public record MessageResponse(String message) {}
}
