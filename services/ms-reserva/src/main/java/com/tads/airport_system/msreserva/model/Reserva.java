package com.tads.airport_system.msreserva.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true)
    private String id;

    @Column(nullable = false)
    private String vooId;

    @Column(nullable = false)
    private String clienteId;

    @Column(name = "dataHoraRes", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime dataHoraRes = LocalDateTime.now();

    @Column(nullable = false)
    private double valor;

    @Column(nullable = false)
    private int quantidadePoltronas;

    @Column(nullable = false)
    private int milhasUtilizadas;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codigo_estado", nullable = false)
    private EstadoReserva estado;

    @JsonIgnore
    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlteracaoEstadoReserva> alteracoesEstado = new ArrayList<>();

    public Reserva(String id, String vooId, String clienteId, EstadoReserva estado, LocalDateTime dataHoraRes, double valor, int quantidadePoltronas, int milhasUtilizadas) {
        this.id = id;
        this.vooId = vooId;
        this.clienteId = clienteId;
        this.estado = estado;
        this.dataHoraRes = LocalDateTime.now();
        this.valor = valor;
        this.quantidadePoltronas = quantidadePoltronas;
        this.milhasUtilizadas = milhasUtilizadas;
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

    public String getClienteId() {
        return clienteId;
    }

    public LocalDateTime getDataHoraRes() {
        return dataHoraRes;
    }

    public double getValor() {
        return valor;
    }

    public int getQuantidadePoltronas() {
        return quantidadePoltronas;
    }

    public int getMilhasUtilizadas() {
        return milhasUtilizadas;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    /**
     * Seta o estado sem criar um registro de mudança de estado
     * @param estado o novo estado
     */
    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    /**
     * Atualiza o estado da reserva e cria um registro de mudança de estado
     * @param novoEstado o novo estado
     * @return o registro da criação da mudança de estado
     */
    public AlteracaoEstadoReserva atualizarEstado(EstadoReserva novoEstado) {
        EstadoReserva estadoAntigo = this.estado;
        this.estado = novoEstado;
        return alterarEstado(estadoAntigo, novoEstado);
    }

    private AlteracaoEstadoReserva alterarEstado(EstadoReserva estadoAntigo, EstadoReserva novoEstado) {
        AlteracaoEstadoReserva alteracaoEstadoReserva = new AlteracaoEstadoReserva(this, estadoAntigo, novoEstado);
        this.alteracoesEstado.add(alteracaoEstadoReserva);
        return alteracaoEstadoReserva;
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

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setQuantidadePoltronas(int quantidadePoltronas) {
        this.quantidadePoltronas = quantidadePoltronas;
    }

    public void setMilhasUtilizadas(int milhasUtilizadas) {
        this.milhasUtilizadas = milhasUtilizadas;
    }

    public List<AlteracaoEstadoReserva> getAlteracoesEstado() {
        return alteracoesEstado;
    }
}