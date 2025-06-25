package com.tads.airport_system.msreserva.dto;

public class ReservaRequestDTO {

    private String codigo_voo;
    private String codigo_cliente;
    private double valor;
    private int quantidade_poltronas;
    private int milhas_utilizadas;
    private String codigo_aeroporto_origem;
    private String codigo_aeroporto_destino;

    public ReservaRequestDTO() {}

    public ReservaRequestDTO(String codigo_voo, String codigo_cliente, double valor, int quantidade_poltronas, int milhas_utilizadas,
                             String codigo_aeroporto_origem, String codigo_aeroporto_destino) {
        this.codigo_voo = codigo_voo;
        this.codigo_cliente = codigo_cliente;
        this.valor = valor;
        this.quantidade_poltronas = quantidade_poltronas;
        this.milhas_utilizadas = milhas_utilizadas;
        this.codigo_aeroporto_origem = codigo_aeroporto_origem;
        this.codigo_aeroporto_destino = codigo_aeroporto_destino;
    }

    public String getCodigo_voo() {
        return codigo_voo;
    }

    public void setCodigo_voo(String codigo_voo) {
        this.codigo_voo = codigo_voo;
    }

    public String getCodigo_cliente() {
        return codigo_cliente;
    }

    public void setCodigo_cliente(String codigo_cliente) {
        this.codigo_cliente = codigo_cliente;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getQuantidade_poltronas() {
        return quantidade_poltronas;
    }

    public void setQuantidade_poltronas(int quantidade_poltronas) {
        this.quantidade_poltronas = quantidade_poltronas;
    }

    public int getMilhas_utilizadas() {
        return milhas_utilizadas;
    }

    public void setMilhas_utilizadas(int milhas_utilizadas) {
        this.milhas_utilizadas = milhas_utilizadas;
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

    @Override
    public String toString() {
        return "ReservaRequestDTO{" +
                "codigo_voo='" + codigo_voo + '\'' +
                ", codigo_cliente='" + codigo_cliente + '\'' +
                ", valor=" + valor +
                ", quantidade_poltronas=" + quantidade_poltronas +
                ", milhas_utilizadas=" + milhas_utilizadas +
                ", codigo_aeroporto_origem='" + codigo_aeroporto_origem + '\'' +
                ", codigo_aeroporto_destino='" + codigo_aeroporto_destino + '\'' +
                '}';
    }
}
