package com.afb.transferplatform.config;

import com.afb.transferplatform.entity.Agent;
import com.afb.transferplatform.entity.StatutTransfert;
import com.afb.transferplatform.entity.Transfert;
import com.afb.transferplatform.repository.AgentRepository;
import com.afb.transferplatform.repository.TransfertRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(AgentRepository agents, TransfertRepository transferts, PasswordEncoder encoder) {
        return args -> {
            if (agents.count() > 0) return;

            Agent agent = new Agent(
                    "Agent DRI 04",
                    "+237690000000",
                    "agent04@afrilandfirstbank.com",
                    "Instant Transfert",
                    encoder.encode("password"),
                    "04",
                    "DEI");
            agents.save(agent);

            Transfert t = new Transfert();
            t.setNomClient("ERIKA");
            t.setDateNaissance("20 mai 2008");
            t.setNaturePiece("Récépissé CNI");
            t.setNumeroPiece("0003907986");
            t.setMontant(20000);
            t.setPaysDestination("France");
            t.setStatut(StatutTransfert.EXECUTE);
            t.setReference("PLT-0098-2026");
            t.setAgence("DEI");
            t.setCanal("MoneyGram");
            t.setDateTransfert(LocalDate.now().withDayOfMonth(2));
            t.setCumulMois(20000);
            t.setAgent(agent);
            transferts.save(t);
        };
    }
}