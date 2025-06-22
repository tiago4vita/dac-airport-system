package com.tads.airport_system.msreserva.dto;

import com.tads.airport_system.msreserva.model.EstadoReserva;
import com.tads.airport_system.msreserva.model.Reserva;
import java.time.LocalDateTime;

import java.util.Objects;

public class ReservaDTO {

    private String id;
    private String vooId;
    private String clienteId;
    private LocalDateTime dataHoraRes;
    private EstadoReserva estado;

    public ReservaDTO() {}

    public ReservaDTO (String id, String vooId, String clienteId, LocalDateTime dataHoraRes, EstadoReserva estado) {
        this.id = id;
        this.vooId = vooId;
        this.clienteId = clienteId;
        this.dataHoraRes = dataHoraRes;
        this.estado = estado;
    }

    public String getId(){
        return id;
    }

    public String getVooId(){
        return vooId;
    }

    public String getClienteId() {
        return clienteId;
    }

    public LocalDateTime getDataHoraRes() {
        return dataHoraRes;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setID(String id) {
        this.id = id;
    }

    public void setVooID(String vooId) {
        this.vooId = vooId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public void setDataHoraRes(LocalDateTime dataHoraRes) {
        this.dataHoraRes = dataHoraRes;
    }

    @Override
    public String toString() {
        return "ReservaDTO{" +
                "id='" + id + '\'' +
                ", vooId='" + vooId + '\'' +
                ", clienteId='" + clienteId + '\'' +
                ", dataHoraRes='" + dataHoraRes + '\'' +
                ", estado=" + estado +
                '}';
    }

}