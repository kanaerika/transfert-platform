package com.afb.transferplatform.controller;
import com.afb.transferplatform.entity.Partenaire;
import com.afb.transferplatform.service.PartenaireService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partenaires")
public class PartenaireController {

    private final PartenaireService service;

    public PartenaireController(PartenaireService service) {
        this.service = service;
    }

    @GetMapping
    public List<Partenaire> getAll(){
        return service.getAll();
    }

    @PostMapping
    public Partenaire create(@RequestBody Partenaire partenaire){
        return service.create(partenaire);
    }

    @PutMapping("/{id}")
    public Partenaire update(
            @PathVariable Long id,
            @RequestBody Partenaire partenaire){

        return service.update(id, partenaire);
    }

    @PatchMapping("/{id}/activer")
    public Partenaire activer(@PathVariable Long id){
        return service.activer(id);
    }

    @PatchMapping("/{id}/desactiver")
    public Partenaire desactiver(@PathVariable Long id){
        return service.desactiver(id);
    }

    @DeleteMapping("/{id}")
    public void supprimer(@PathVariable Long id){
        service.supprimer(id);
    }

    @PostMapping("/{id}/invitation")
    public void invitation(@PathVariable Long id){
        service.envoyerInvitation(id);
    }

    @GetMapping("/{id}")
    public Partenaire getById(@PathVariable Long id){
        return service.getById(id);
    }
}
