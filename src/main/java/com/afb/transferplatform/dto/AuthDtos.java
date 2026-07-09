package com.afb.transferplatform.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public record LoginRequest(
            @NotBlank String telephone,
            @NotBlank String motDePasse) {}

    public record RegisterRequest(
            @NotBlank String nomComplet,
            @NotBlank String telephone,
            @NotBlank @Email String email,
            @NotBlank String role,
            @NotBlank String motDePasse,
            @NotBlank String confirmationMotDePasse) {}

    public record AuthResponse(
            String token,
            Long agentId,
            String nomComplet,
            String codeAgent,
            String role) {}
}