package com.tads.airport_system.msreserva.dto;

public class EfetuarReservaDTO {

    private String codigo_voo;
    private String codigo_cliente;
    private double valor;
    private int quantidadePoltronas;
    private int milhasUtilizadas;

    public EfetuarReservaDTO() {}

    public EfetuarReservaDTO(String codigo_voo, String codigo_cliente, double valor, int quantidadePoltronas, int milhasUtilizadas) {
        this.codigo_voo = codigo_voo;
        this.codigo_cliente = codigo_cliente;
        this.valor = valor;
        this.quantidadePoltronas = quantidadePoltronas;
        this.milhasUtilizadas = milhasUtilizadas;
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

    public int getQuantidadePoltronas() {
        return quantidadePoltronas;
    }

    public void setQuantidadePoltronas(int quantidadePoltronas) {
        this.quantidadePoltronas = quantidadePoltronas;
    }

    public int getMilhasUtilizadas() {
        return milhasUtilizadas;
    }

    public void setMilhasUtilizadas(int milhasUtilizadas) {
        this.milhasUtilizadas = milhasUtilizadas;
    }

    @Override
    public String toString() {
        return "EfetuarReservaDTO{" +
                "codigo_voo='" + codigo_voo + '\'' +
                ", codigo_cliente='" + codigo_cliente + '\'' +
                ", valor=" + valor +
                ", quantidadePoltronas=" + quantidadePoltronas +
                ", milhasUtilizadas=" + milhasUtilizadas +
                '}';
    }
}