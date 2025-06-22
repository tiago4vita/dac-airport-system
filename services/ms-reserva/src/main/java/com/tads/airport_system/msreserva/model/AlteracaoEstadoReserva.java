package com.tads.airport_system.msreserva.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "alteracoes_estado_reserva")
public class AlteracaoEstadoReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @Column(name = "data_hora_alteracao", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime dataHoraAlteracao = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_origem_id", nullable = false)
    private EstadoReserva estadoOrigem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_destino_id", nullable = false)
    private EstadoReserva estadoDestino;

    // Construtores
    public AlteracaoEstadoReserva() {
    }

    public AlteracaoEstadoReserva(Reserva reserva, EstadoReserva estadoOrigem, EstadoReserva estadoDestino) {
        this.reserva = reserva;
        this.estadoOrigem = estadoOrigem;
        this.estadoDestino = estadoDestino;
        this.dataHoraAlteracao = LocalDateTime.now();
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public LocalDateTime getDataHoraAlteracao() {
        return dataHoraAlteracao;
    }

    public void setDataHoraAlteracao(LocalDateTime dataHoraAlteracao) {
        this.dataHoraAlteracao = dataHoraAlteracao;
    }

    public EstadoReserva getEstadoOrigem() {
        return estadoOrigem;
    }

    public void setEstadoOrigem(EstadoReserva estadoOrigem) {
        this.estadoOrigem = estadoOrigem;
    }

    public EstadoReserva getEstadoDestino() {
        return estadoDestino;
    }

    public void setEstadoDestino(EstadoReserva estadoDestino) {
        this.estadoDestino = estadoDestino;
    }

    // Equals e HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlteracaoEstadoReserva that = (AlteracaoEstadoReserva) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AlteracaoEstadoReserva{" +
                "id='" + id + '\'' +
                ", reserva=" + (reserva != null ? reserva.getId() : "null") +
                ", dataHoraAlteracao=" + dataHoraAlteracao +
                ", estadoOrigem=" + (estadoOrigem != null ? estadoOrigem.getCodigoEstado() : "null") +
                ", estadoDestino=" + (estadoDestino != null ? estadoDestino.getCodigoEstado() : "null") +
                '}';
    }
}
