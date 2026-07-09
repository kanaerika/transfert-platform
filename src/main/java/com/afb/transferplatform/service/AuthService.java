package com.afb.transferplatform.service;


import com.afb.transferplatform.dto.AuthDtos.*;
import com.afb.transferplatform.entity.Agent;
import com.afb.transferplatform.repository.AgentRepository;
import com.afb.transferplatform.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AgentRepository agentRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.agentRepository = agentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        Agent agent = agentRepository.findByTelephone(normaliser(request.telephone()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Identifiants incorrects."));
        if (!passwordEncoder.matches(request.motDePasse(), agent.getMotDePasse())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants incorrects.");
        }
        return toResponse(agent);
    }

    public AuthResponse register(RegisterRequest request) {
        if (!request.motDePasse().equals(request.confirmationMotDePasse())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Les mots de passe ne correspondent pas.");
        }
        String tel = normaliser(request.telephone());
        if (agentRepository.existsByTelephone(tel)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Un compte existe déjà avec ce numéro de téléphone.");
        }
        if (agentRepository.existsByEmail(request.email().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Un compte existe déjà avec cet email.");
        }
        Agent agent = new Agent(
                request.nomComplet().trim(),
                tel,
                request.email().toLowerCase(),
                request.role(),
                passwordEncoder.encode(request.motDePasse()),
                genererCodeAgent(),
                "DEI");
        agentRepository.save(agent);
        return toResponse(agent);
    }

    private AuthResponse toResponse(Agent agent) {
        String token = jwtService.generateToken(agent.getId(), agent.getTelephone());
        return new AuthResponse(token, agent.getId(), agent.getNomComplet(),
                agent.getCodeAgent(), agent.getRole());
    }

    private String normaliser(String telephone) {
        return telephone.replaceAll("[\\s.-]", "");
    }

    private String genererCodeAgent() {
        long n = agentRepository.count() + 1;
        return String.format("%02d", n);
    }
}