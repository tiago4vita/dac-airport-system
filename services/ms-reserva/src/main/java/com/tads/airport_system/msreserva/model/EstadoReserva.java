package com.tads.airport_system.msreserva.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@Entity
@Table(name = "estados_reserva")
public class EstadoReserva {

    @Id
    @Column(name = "codigo_estado", unique = true, nullable = false)
    private String codigoEstado;

    @Column(name = "sigla", nullable = false, length = 20)
    private String sigla;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    // enum para os estados pré-definidos
    public enum Estado {
        CRIADA("CRIADA", "CR", "Reserva criada"),
        CHECK_IN("CHECK-IN", "CI", "Check-in realizado"),
        CANCELADA("CANCELADA", "CA", "Reserva cancelada"),
        CANCELADA_VOO("CANCELADA VOO", "CV", "Reserva cancelada devido ao cancelamento do voo"),
        EMBARCADA("EMBARCADA", "EM", "Passageiro embarcado"),
        REALIZADA("REALIZADA", "RE", "Reserva realizada com sucesso"),
        NAO_REALIZADA("NÃO REALIZADA", "NR", "Reserva não realizada");

        private final String descricao;
        private final String sigla;
        private final String nome;

        Estado(String nome, String sigla, String descricao) {
            this.nome = nome;
            this.sigla = sigla;
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getSigla() {
            return sigla;
        }

        public String getNome() {
            return nome;
        }
    }

    // Construtores
    public EstadoReserva() {
    }

    public EstadoReserva(String codigoEstado, String sigla, String descricao) {
        this.codigoEstado = codigoEstado;
        this.sigla = sigla;
        this.descricao = descricao;
    }

    // Factory method para criar a partir do enum
    public static EstadoReserva fromEnum(Estado estado) {
        return new EstadoReserva(
                estado.name(),
                estado.getSigla(),
                estado.getDescricao()
        );
    }

    // Getters e Setters
    public String getCodigoEstado() {
        return codigoEstado;
    }

    public void setCodigoEstado(String codigoEstado) {
        this.codigoEstado = codigoEstado;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    // Equals e HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EstadoReserva that = (EstadoReserva) o;
        return Objects.equals(codigoEstado, that.codigoEstado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoEstado);
    }

    @Override
    public String toString() {
        return "EstadoReserva{" +
                "codigoEstado='" + codigoEstado + '\'' +
                ", sigla='" + sigla + '\'' +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
