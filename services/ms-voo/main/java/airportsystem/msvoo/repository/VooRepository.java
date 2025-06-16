package airportsystem.msvoo.repository;

import airportsystem.msvoo.model.Voo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface VooRepository extends JpaRepository<Voo, Long> {
    @Query("SELECT v FROM Voo v JOIN v.origem JOIN v.destino")
    List<Voo> findAllWithOrigemAndDestino();
    
    @Query("SELECT v FROM Voo v JOIN v.origem JOIN v.destino WHERE v.id = :id")
    Optional<Voo> findByIdWithOrigemAndDestino(@Param("id") Long id);
    
    @Query("SELECT v FROM Voo v WHERE v.origem.codigo = :origemCodigo " +
           "AND v.destino.codigo = :destinoCodigo " +
           "AND v.status = airportsystem.msvoo.enums.StatusVoos.CONFIRMADO")
    List<Voo> findByOrigemCodigoAndDestinoCodigo(
        @Param("origemCodigo") String origemCodigo, 
        @Param("destinoCodigo") String destinoCodigo
    );
    
    @Query("SELECT DISTINCT v FROM Voo v " +
            "LEFT JOIN FETCH v.origem o " +
            "LEFT JOIN FETCH v.destino d " +
            "LEFT JOIN FETCH v.reservasTracking rt " +
            "WHERE v.id = :id")
    Optional<Voo> findByIdWithReservas(@Param("id") Long id);
}