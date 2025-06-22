package com.tads.airport_system.msreserva.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservas_view")
public class ReservaView {
    @Id
    private String id;
    private String vooId;
    private LocalDateTime dataHoraRes;
    private double valor;
    private int quantidadePoltronas;
    private int milhasUtilizadas;
    private String estadoCodigo;
    private String estadoSigla;
    private String estadoDescricao;

    public ReservaView() {
    }

    public ReservaView(String id, String vooId, LocalDateTime dataHoraRes, double valor, int quantidadePoltronas, int milhasUtilizadas, String estadoCodigo, String estadoSigla, String estadoDescricao) {
        this.id = id;
        this.vooId = vooId;
        this.dataHoraRes = dataHoraRes;
        this.valor = valor;
        this.quantidadePoltronas = quantidadePoltronas;
        this.milhasUtilizadas = milhasUtilizadas;
        this.estadoCodigo = estadoCodigo;
        this.estadoSigla = estadoSigla;
        this.estadoDescricao = estadoDescricao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVooId() {
        return vooId;
    }

    public void setVooId(String vooId) {
        this.vooId = vooId;
    }

    public LocalDateTime getDataHoraRes() {
        return dataHoraRes;
    }

    public void setDataHoraRes(LocalDateTime dataHoraRes) {
        this.dataHoraRes = dataHoraRes;
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

    public String getEstadoCodigo() {
        return estadoCodigo;
    }

    public void setEstadoCodigo(String estadoCodigo) {
        this.estadoCodigo = estadoCodigo;
    }

    public String getEstadoSigla() {
        return estadoSigla;
    }

    public void setEstadoSigla(String estadoSigla) {
        this.estadoSigla = estadoSigla;
    }

    public String getEstadoDescricao() {
        return estadoDescricao;
    }

    public void setEstadoDescricao(String estadoDescricao) {
        this.estadoDescricao = estadoDescricao;
    }

    @Override
    public String toString() {
        return "ReservaView{" +
                "id='" + id + '\'' +
                ", vooId='" + vooId + '\'' +
                ", dataHoraRes=" + dataHoraRes +
                ", valor=" + valor +
                ", quantidadePoltronas=" + quantidadePoltronas +
                ", milhasUtilizadas=" + milhasUtilizadas +
                ", estadoCodigo='" + estadoCodigo + '\'' +
                ", estadoSigla='" + estadoSigla + '\'' +
                ", estadoDescricao='" + estadoDescricao + '\'' +
                '}';
    }
}
