package airportsystem.msauth.repository;

import airportsystem.msauth.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    boolean existsByLogin(String login);
    boolean existsByCodigo(String codigo);

    Usuario findByLogin(String login);
}