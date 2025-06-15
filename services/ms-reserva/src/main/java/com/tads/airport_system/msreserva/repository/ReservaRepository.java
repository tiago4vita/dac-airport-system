package airportsystem.msreserva.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, String>{

    Reserva findById(String id);

    boolean existsById(String id);
}