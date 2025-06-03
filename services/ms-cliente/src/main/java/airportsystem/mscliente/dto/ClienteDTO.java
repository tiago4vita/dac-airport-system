package airportsystem.mscliente.dto;

import airportsystem.mscliente.model.CPF;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public class ClienteDTO {

    @CPF(message = "CPF Inválido")
        private String cpf;

    @Email(message = "Email inválido")
    @NotBlank(message = "Email é obrigatório")
    private String email;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private Long saldoMilhas = 0L;

    @Valid
    private EnderecoDTO endereco;

    // Default constructor
    public ClienteDTO() {}

    // Constructor with all fields
    public ClienteDTO(String cpf, String email, String nome, Long saldoMilhas, EnderecoDTO endereco) {
        this.cpf = cpf;
        this.email = email;
        this.nome = nome;
        this.saldoMilhas = saldoMilhas != null ? saldoMilhas : 0L;
        this.endereco = endereco;
    }

    // Constructor without saldoMilhas (uses default value)
    public ClienteDTO(String cpf, String email, String nome, EnderecoDTO endereco) {
        this.cpf = cpf;
        this.email = email;
        this.nome = nome;
        this.saldoMilhas = 0L;
        this.endereco = endereco;
    }

    // Getters and Setters
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getSaldoMilhas() {
        return saldoMilhas;
    }

    public void setSaldoMilhas(Long saldoMilhas) {
        this.saldoMilhas = saldoMilhas;
    }

    public EnderecoDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoDTO endereco) {
        this.endereco = endereco;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClienteDTO that = (ClienteDTO) o;
        return Objects.equals(cpf, that.cpf) &&
                Objects.equals(email, that.email) &&
                Objects.equals(nome, that.nome) &&
                Objects.equals(saldoMilhas, that.saldoMilhas) &&
                Objects.equals(endereco, that.endereco);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf, email, nome, saldoMilhas, endereco);
    }

    @Override
    public String toString() {
        return "ClienteDTO{" +
                "cpf='" + cpf + '\'' +
                ", email='" + email + '\'' +
                ", nome='" + nome + '\'' +
                ", saldoMilhas=" + saldoMilhas +
                ", endereco=" + endereco +
                '}';
    }
}
