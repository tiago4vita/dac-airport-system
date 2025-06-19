package com.tads.airport_system.msreserva.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true)
    private String id;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true)
    private String vooId;

    @Column(name = "dataHoraRes", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime dataHoraRes = LocalDateTime.now();

    @Column(nullable = false)
    private String estado;

    public Reserva(String id, String vooId, String estado, LocalDateTime dataHoraRes) {
        this.id = id;
        this.vooId = vooId;
        this.estado = estado;
        this.dataHoraRes = LocalDateTime.now();
    }

    public Reserva() {

    }

    public Reserva(String id) {
    }

    //getters e setters
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
}