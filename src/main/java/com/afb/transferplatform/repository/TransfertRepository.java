package com.afb.transferplatform.repository;


import com.afb.transferplatform.entity.StatutTransfert;
import com.afb.transferplatform.entity.Transfert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransfertRepository extends JpaRepository<Transfert, Long> {

    List<Transfert> findAllByOrderByIdDesc();

    List<Transfert> findByStatutOrderByIdDesc(StatutTransfert statut);

    Optional<Transfert> findFirstByNomClientIgnoreCaseOrderByIdDesc(String nomClient);

    /** Cumul Hors CEMAC du mois : somme des transferts EXECUTES du client sur la période. */
    @Query("""
           SELECT COALESCE(SUM(t.montant), 0)
           FROM Transfert t
           WHERE UPPER(t.nomClient) = UPPER(:nomClient)
             AND t.statut = com.afb.transferplatform.entity.StatutTransfert.EXECUTE
             AND t.dateTransfert BETWEEN :debut AND :fin
           """)
    long cumulMensuel(@Param("nomClient") String nomClient,
                      @Param("debut") LocalDate debut,
                      @Param("fin") LocalDate fin);
}