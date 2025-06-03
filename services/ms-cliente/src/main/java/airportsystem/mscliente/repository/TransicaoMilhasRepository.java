package airportsystem.mscliente.repository;

import airportsystem.mscliente.model.TransacaoMilhas;

import java.util.List;

public interface TransicaoMilhasRepository {

    List<TransacaoMilhas> findByClienteIdOrderByDataHoraDesc(String id);
}
