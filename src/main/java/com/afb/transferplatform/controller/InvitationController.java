package com.afb.transferplatform.controller;

import com.afb.transferplatform.entity.Agent;
import com.afb.transferplatform.service.InvitationService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/invitation")
@CrossOrigin(origins = "http://localhost:4200")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @GetMapping("/{token}")
    public Agent verifier(@PathVariable String token) {
        return invitationService.verifierToken(token);
    }

    @PostMapping("/activer")
    public void activer(@RequestBody Map<String, String> body) {

        invitationService.activerCompte(
                body.get("token"),
                body.get("motDePasse")
        );
    }

}