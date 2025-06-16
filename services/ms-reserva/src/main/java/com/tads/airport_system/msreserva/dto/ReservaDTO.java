package com.tads.airport_system.msreserva.dto;

import com.tads.airport_system.msreserva.model.Reserva;
import java.time.LocalDateTime;

import java.util.Objects;

public class ReservaDTO {

    public ReservaDTO() {}

    public ReservaDTO (Long id, String vooId, LocalDateTime dataHoraRes, String estado) {
        this.id = id;
        this.vooId = vooId;
        this.dataHoraRes = dataHoraRes;
        this.estado = estado;
    }

    public String getId(){
        return id;
    }

    public String getVooId(){
        return vooId;
    }

    public LocalDateTime getDataHoraRes() {
        return dataHoraRes;
    }

    public String getEstado() {
        return estado;
    }

    public void setID(String id) {
        this.id = id;
    }

    public void setVooID(String vooId) {
        this.vooId = vooId;
    }

    public void setDataHoraRes(LocalDateTime dataHoraRes) {
        this.dataHoraRes = dataHoraRes;
    }

    @Override
    public String toString() {
        return "ReservaDTO{" +
                "id='" + id + '\'' +
                ", vooId='" + vooId + '\'' +
                ", dataHoraRes='" + dataHoraRes + '\'' +
                ", estado=" + estado +
                '}';
    }

}