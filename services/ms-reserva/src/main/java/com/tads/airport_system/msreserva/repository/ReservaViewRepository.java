package com.tads.airport_system.msreserva.repository;

import com.tads.airport_system.msreserva.model.ReservaView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para enfileirar entidades ReservaView do BD de leitura.
 * É parte do padrão CQRS. Esse repositório é usado EXCLUSIVAMENTE para
 * oprações de leitura dos dados desnormalizados.
 */
@Repository
public interface ReservaViewRepository extends JpaRepository<ReservaView, String> {
    
    /**
     * Acha uma view da reserva pelo id
     * @param id o id da reserva
     * @return a view da reserva
     */
    ReservaView findByIdEquals(String id);
    
    /**
     * Acha a view da reserva pelo id do voo
     * @param vooId o id do voo
     * @return a view da reserva
     */
    ReservaView findByVooId(String vooId);
    
    /**
     * Checa se a view da reserva existe por id
     * @param id o id da reserva
     * @return true se existe, se não, false
     */
    boolean existsById(String id);
}