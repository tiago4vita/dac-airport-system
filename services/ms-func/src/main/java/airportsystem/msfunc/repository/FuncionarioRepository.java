package airportsystem.msfunc.repository;

import airportsystem.msfunc.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, String> {

    Funcionario findByEmail(String email);
    Funcionario findByCpf(String cpf);
    
    // Find by email and ativo status
    Optional<Funcionario> findByEmailAndAtivo(String email, Boolean ativo);
    
    // Find by CPF and ativo status
    Optional<Funcionario> findByCpfAndAtivo(String cpf, Boolean ativo);
    
    // Find all ativo funcionarios
    List<Funcionario> findByAtivo(Boolean ativo);
    
    // Find all ativo funcionarios
    List<Funcionario> findByAtivoTrue();

    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    
    // Check if exists by email and ativo status
    boolean existsByEmailAndAtivo(String email, Boolean ativo);
    
    // Check if exists by CPF and ativo status
    boolean existsByCpfAndAtivo(String cpf, Boolean ativo);

}
