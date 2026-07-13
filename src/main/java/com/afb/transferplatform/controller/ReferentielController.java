package com.afb.transferplatform.controller;
 
import com.afb.transferplatform.dto.TransfertDtos.CanalDto;
import com.afb.transferplatform.dto.TransfertDtos.ReferentielResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
import java.util.List;
 
/** Listes de référence pour le frontend (natures de pièce, pays, rôles, canaux). */
@RestController
@RequestMapping("/api/referentiel")
public class ReferentielController {
 
    private final long plafond;
 
    public ReferentielController(@Value("${app.plafond-mensuel:1000000}") long plafond) {
        this.plafond = plafond;
    }
 
    @GetMapping
    public ReferentielResponse referentiel() {
        return new ReferentielResponse(
                List.of("Carte Nationale d'Identité", "Récépissé CNI", "Passeport",
                        "Carte de séjour", "Permis de conduire"),
                List.of("Cameroun", "Congo", "Gabon", "Guinée équatoriale",
                        "République centrafricaine", "Tchad",
                        "France", "Belgique", "Canada", "États-Unis",
                        "Samoa américaines", "Chine", "Sénégal", "Nigéria"),
                List.of("Instant Transfert", "Financial House", "Julie Voyage",
                        "Express Union", "Caisse"),
                List.of(new CanalDto("MoneyGram", "Transfert international · réseau MoneyGram"),
                        new CanalDto("Small World", "Transfert international · réseau Small World")),
                plafond);
    }
}
