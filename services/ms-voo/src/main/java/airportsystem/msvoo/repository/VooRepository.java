package airportsystem.msvoo.repository;

import airportsystem.msvoo.model.Voo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VooRepository extends JpaRepository<Voo, String> {

    Voo findByCodigo(String codigo);
    Voo findByOrigem(String origem);
    Voo findByDestino(String destino);

    List<Voo> findByDataHoraBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    boolean existsByCodigo(String codigo);
    boolean existsByOrigem(String origem);
    boolean existsByDestino(String destino);

}
