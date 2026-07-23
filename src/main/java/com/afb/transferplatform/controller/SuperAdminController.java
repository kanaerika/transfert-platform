package com.afb.transferplatform.controller;

import com.afb.transferplatform.dto.AuthDtos.*;
import com.afb.transferplatform.service.SuperAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoints du Super Administrateur (Afriland First Bank). */
@RestController
@RequestMapping("/api/superadmin/partenaires")
public class SuperAdminController {

    private final SuperAdminService service;

    public SuperAdminController(SuperAdminService service) { this.service = service; }

    @GetMapping
    public List<PartenaireResponse> lister() { return service.lister(); }

    @PostMapping
    public PartenaireResponse creer(@Valid @RequestBody PartenaireRequest req) {
        return service.creer(req);
    }

    @PutMapping("/{id}")
    public PartenaireResponse modifier(@PathVariable Long id,
                                       @Valid @RequestBody ModificationPartenaireRequest req) {
        return service.modifier(id, req);
    }

    @PatchMapping("/{id}/activation")
    public PartenaireResponse basculerActif(@PathVariable Long id) {
        return service.basculerActif(id);
    }

    @PostMapping("/{id}/invitation")
    public MessageResponse reinviter(@PathVariable Long id) {
        return service.reinviter(id);
    }
}
