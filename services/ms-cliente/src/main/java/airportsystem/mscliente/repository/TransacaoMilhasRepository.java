package airportsystem.mscliente.repository;

import airportsystem.mscliente.model.TransacaoMilhas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoMilhasRepository extends JpaRepository<TransacaoMilhas, Long> {
    List<TransacaoMilhas> findByClienteCodigoOrderByDataHoraDesc(String codigo);
}