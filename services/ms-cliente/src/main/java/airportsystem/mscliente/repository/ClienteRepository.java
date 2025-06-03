package airportsystem.mscliente.repository;

import airportsystem.mscliente.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {

    Cliente findByEmail(String email);
    Cliente findByCpf(String cpf);

    boolean existsByEmail(String email);
}
