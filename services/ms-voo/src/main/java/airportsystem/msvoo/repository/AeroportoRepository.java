package airportsystem.msvoo.repository;

import airportsystem.msvoo.model.Aeroporto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AeroportoRepository extends JpaRepository<Aeroporto, String> {

    Aeroporto findByCodigo(String codigo);
    Aeroporto findByNome(String nome);
    Aeroporto findByCidade(String cidade);
    
    boolean existsByCodigo(String codigo);
    boolean existsByNome(String nome);
    boolean existsByCidade(String cidade);

}
