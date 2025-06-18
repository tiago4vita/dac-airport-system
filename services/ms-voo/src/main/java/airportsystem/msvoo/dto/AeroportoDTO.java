package airportsystem.msvoo.dto;

import jakarta.persistence.Id;
import java.util.Objects;

public class AeroportoDTO {

    @Id
    private String codigo;

    private String nome;

    private String cidade;

    private String UF;

    // Default constructor
    public AeroportoDTO() {}

    // Constructor with all fields
    public AeroportoDTO(String codigo, String nome, String cidade, String UF) {
        this.codigo = codigo;
        this.nome = nome;
        this.cidade = cidade;
        this.UF = UF;
    }

    // Getters and Setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUF() {
        return UF;
    }

    public void setUF(String UF) {
        this.UF = UF;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AeroportoDTO that = (AeroportoDTO) o;
        return Objects.equals(codigo, that.codigo) &&
               Objects.equals(nome, that.nome) &&
               Objects.equals(cidade, that.cidade) &&
               Objects.equals(UF, that.UF);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo, nome, cidade, UF);
    }

    @Override
    public String toString() {
        return "AeroportoDTO{" +
                "codigo='" + codigo + '\'' +
                ", nome='" + nome + '\'' + 
                ", cidade='" + cidade + '\'' +
                ", UF='" + UF + '\'' +
                '}';
    }
}
