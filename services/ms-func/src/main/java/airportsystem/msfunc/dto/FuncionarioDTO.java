package airportsystem.msfunc.dto;

import java.time.LocalDateTime;

public class FuncionarioDTO {
    private String codigo;
    private String cpf;
    private String nome;
    private String email;
    private String telefone;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private Boolean ativo;

    public FuncionarioDTO() {
        this.ativo = true;
    }

    public FuncionarioDTO(String cpf, String nome, String email, String telefone) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.dataCriacao = LocalDateTime.now();
        this.ativo = true;
    }

    public FuncionarioDTO(String cpf, String nome, String email, String telefone, Boolean ativo) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.dataCriacao = LocalDateTime.now();
        this.ativo = ativo != null ? ativo : true;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
