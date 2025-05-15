package com.tads.airport_system.ms_cliente.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "transacoes_milhas")
data class TransacaoMilhas(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_cpf", nullable = false)
    val cliente: Cliente,

    @Column(name = "data_hora", nullable = false)
    val dataHora: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val quantidade: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipo: TipoTransacao,

    @Column(nullable = false)
    val descricao: String
) {
    enum class TipoTransacao {
        ENTRADA, SAIDA
    }
} 