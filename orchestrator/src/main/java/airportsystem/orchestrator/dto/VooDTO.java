package airportsystem.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public class VooDTO {

    @JsonProperty("data")
    private OffsetDateTime data;

    @JsonProperty("valor_passagem")
    private float valor_passagem;

    @JsonProperty("quantidade_poltronas_total")
    private int quantidade_poltronas_total;

    @JsonProperty("quantidade_poltronas_ocupadas")
    private int quantidade_poltronas_ocupadas;

    @JsonProperty("codigo_aeroporto_origem")
    private String codigo_aeroporto_origem;

    @JsonProperty("codigo_aeroporto_destino")
    private String codigo_aeroporto_destino;

    // Getters e Setters

    public OffsetDateTime getData() {
        return data;
    }

    public void setData(OffsetDateTime data) {
        this.data = data;
    }

    public float getValor_passagem() {
        return valor_passagem;
    }

    public void setValor_passagem(float valor_passagem) {
        this.valor_passagem = valor_passagem;
    }

    public int getQuantidade_poltronas_total() {
        return quantidade_poltronas_total;
    }

    public void setQuantidade_poltronas_total(int quantidade_poltronas_total) {
        this.quantidade_poltronas_total = quantidade_poltronas_total;
    }

    public int getQuantidade_poltronas_ocupadas() {
        return quantidade_poltronas_ocupadas;
    }

    public void setQuantidade_poltronas_ocupadas(int quantidade_poltronas_ocupadas) {
        this.quantidade_poltronas_ocupadas = quantidade_poltronas_ocupadas;
    }

    public String getCodigo_aeroporto_origem() {
        return codigo_aeroporto_origem;
    }

    public void setCodigo_aeroporto_origem(String codigo_aeroporto_origem) {
        this.codigo_aeroporto_origem = codigo_aeroporto_origem;
    }

    public String getCodigo_aeroporto_destino() {
        return codigo_aeroporto_destino;
    }

    public void setCodigo_aeroporto_destino(String codigo_aeroporto_destino) {
        this.codigo_aeroporto_destino = codigo_aeroporto_destino;
    }
}
