package airportsystem.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClienteRequestDTO {
    private String cpf;
    private String email;
    private String nome;
    
    @JsonProperty("saldo_milhas")
    private Long saldoMilhas;
    private EnderecoRequestDTO endereco;
    private String senha;

    public ClienteRequestDTO() {
    }

    public ClienteRequestDTO(String cpf, String email, String nome, Long saldoMilhas, EnderecoRequestDTO endereco, String senha) {
        this.cpf = cpf;
        this.email = email;
        this.nome = nome;
        this.saldoMilhas = saldoMilhas;
        this.endereco = endereco;
        this.senha = senha;
    }

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

    public EnderecoRequestDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoRequestDTO endereco) {
        this.endereco = endereco;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
} 