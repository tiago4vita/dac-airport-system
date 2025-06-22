package com.tads.airport_system.msreserva.dto;

public class EfetuarReservaDTO {

    private String codigo_voo;
    private String codigo_cliente;

    public EfetuarReservaDTO() {}

    public EfetuarReservaDTO(String codigo_voo, String codigo_cliente) {
        this.codigo_voo = codigo_voo;
        this.codigo_cliente = codigo_cliente;
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

    @Override
    public String toString() {
        return "EfetuarReservaDTO{" +
                "codigo_voo='" + codigo_voo + '\'' +
                ", codigo_cliente='" + codigo_cliente + '\'' +
                '}';
    }
}