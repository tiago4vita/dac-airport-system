package com.tads.airport_system.msreserva.repository;

import com.tads.airport_system.msreserva.model.AlteracaoEstadoReserva;
import com.tads.airport_system.msreserva.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "commandTransactionManager")
public interface AlteracaoEstadoReservaRepository extends JpaRepository<AlteracaoEstadoReserva, String> {
    
    /**
     * Encontra todas as mudanças de estado para uma unica reserva
     * @param reserva a reserva
     * @return lista de alteracao de estado
     */
    List<AlteracaoEstadoReserva> findByReservaOrderByDataHoraAlteracaoDesc(Reserva reserva);
    
    /**
     * Encontra todas as mudanças de estado de acordo com um id especifico de reserva
     * @param reservaId o estado de reserva
     * @return lista de mudança de estado
     */
    List<AlteracaoEstadoReserva> findByReserva_IdOrderByDataHoraAlteracaoDesc(String reservaId);
    
    /**
     * Checa se tem alguma mudança de estado para uma reserva especifica
     * @param reserva a reserva
     * @return true se há mudanças de estado, se não retorna false
     */
    boolean existsByReserva(Reserva reserva);
}