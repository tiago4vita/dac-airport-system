package com.tads.airport_system.msreserva.event;

import com.tads.airport_system.msreserva.model.Reserva;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Classe de evento para eventos relacionados a reservas.
 * Usado no padrão CQRS para comunicar mudanças entre os lados de leitura e comendo no BD.
 */
public class ReservaEvent implements Serializable {
    
    private String tipo;
    private String reservaId;
    private String vooId;
    private LocalDateTime dataHoraRes;
    private String estadoCodigo;
    private String estadoSigla;
    private String estadoDescricao;
    private LocalDateTime dataHora;
    
    // Construtor padrão pra serialização
    public ReservaEvent() {
        this.dataHora = LocalDateTime.now();
    }
    
    /**
     * Constructor com tipo de evento e reserva
     * @param tipo o tipo de evento (ex: "RESERVA_CRIADA", "RESERVA_CANCELADA")
     * @param reserva o objeto da reserva
     */
    public ReservaEvent(String tipo, Reserva reserva) {
        this.tipo = tipo;
        this.reservaId = reserva.getId();
        this.vooId = reserva.getVooId();
        this.dataHoraRes = reserva.getDataHoraRes();
        this.estadoCodigo = reserva.getEstado().getCodigoEstado();
        this.estadoSigla = reserva.getEstado().getSigla();
        this.estadoDescricao = reserva.getEstado().getDescricao();
        this.dataHora = LocalDateTime.now();
    }
    
    // getters e setters
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getReservaId() {
        return reservaId;
    }
    
    public void setReservaId(String reservaId) {
        this.reservaId = reservaId;
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
    
    public LocalDateTime getDataHora() {
        return dataHora;
    }
    
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
    
    @Override
    public String toString() {
        return "ReservaEvent{" +
                "tipo='" + tipo + '\'' +
                ", reservaId='" + reservaId + '\'' +
                ", vooId='" + vooId + '\'' +
                ", dataHoraRes=" + dataHoraRes +
                ", estadoCodigo='" + estadoCodigo + '\'' +
                ", estadoSigla='" + estadoSigla + '\'' +
                ", estadoDescricao='" + estadoDescricao + '\'' +
                ", dataHora=" + dataHora +
                '}';
    }
}