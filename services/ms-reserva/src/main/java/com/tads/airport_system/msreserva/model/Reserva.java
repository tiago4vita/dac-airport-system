package com.tads.airport_system.msreserva.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

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

    @Column(unique = true, nullable = false)
    private String vooId;

    @Column(name = "dataHoraRes", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime dataHoraRes = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codigo_estado", nullable = false)
    private EstadoReserva estado;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlteracaoEstadoReserva> alteracoesEstado = new ArrayList<>();

    public Reserva(String id, String vooId, EstadoReserva estado, LocalDateTime dataHoraRes) {
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

    public void setID(String id) {
        this.id = id;
    }

    public void setVooID(String vooId) {
        this.vooId = vooId;
    }

    public void setDataHoraRes(LocalDateTime dataHoraRes) {
        this.dataHoraRes = dataHoraRes;
    }

    public List<AlteracaoEstadoReserva> getAlteracoesEstado() {
        return alteracoesEstado;
    }

    public void setAlteracoesEstado(List<AlteracaoEstadoReserva> alteracoesEstado) {
        this.alteracoesEstado = alteracoesEstado;
    }

    /**
     * Adiciona uma mudança de estado para a reserva e seta a reserva na mudança de estado
     * @param alteracaoEstado a mudança de estado a adicionar
     */
    public void addAlteracaoEstado(AlteracaoEstadoReserva alteracaoEstado) {
        alteracoesEstado.add(alteracaoEstado);
        alteracaoEstado.setReserva(this);
    }

    /**
     * Remove uma mudança de estado da reserva
     * @param alteracaoEstado a mudança de estado a remover
     */
    public void removeAlteracaoEstado(AlteracaoEstadoReserva alteracaoEstado) {
        alteracoesEstado.remove(alteracaoEstado);
        alteracaoEstado.setReserva(null);
    }

    /**
     * Cria e adiciona uma nova mudança de estado para a reserva
     * @param estadoOrigem o estado original
     * @param estadoDestino o estado a ser registrado
     * @return a alteração de mudança de estado criada
     */
    public AlteracaoEstadoReserva alterarEstado(EstadoReserva estadoOrigem, EstadoReserva estadoDestino) {
        AlteracaoEstadoReserva alteracao = new AlteracaoEstadoReserva(this, estadoOrigem, estadoDestino);
        addAlteracaoEstado(alteracao);
        return alteracao;
    }
}
