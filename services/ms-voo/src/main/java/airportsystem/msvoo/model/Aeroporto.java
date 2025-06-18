package airportsystem.msvoo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Aeroporto {

    @Id
    private String codigo;

    private String nome;

    private String cidade;

    private String UF;

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public String getCidade() {
        return cidade;
    }

    public String getUF() {
        return UF;
    }
    
}
