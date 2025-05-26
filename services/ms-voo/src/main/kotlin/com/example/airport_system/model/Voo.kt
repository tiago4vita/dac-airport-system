package com.example.airport_system.ms_voo.model

import jakarta.persistence.*
import java.time.LocalDateTime
import org.hibernate.annotations.GenericGenerator


@Entity
@Table(name = "voo")
data class Voo(
    @Id
    @GeneratedValue(generator = "tads-id-generator")
    @GenericGenerator(
        name = "tads-id-generator",
        strategy = "com.tads.airport_system.ms_voo.generator.TadsIdGenerator"
    )
    val id: String = "", // Alterado para String para permitir IDs alfanuméricos
    var origem: String,
    var destino: String,
    var dataHora: LocalDateTime,
    var preco: Double,
    var qtdPoltronas: Int = 0,
    var status: String = "Confirmado" // Status do voo (ex: "Pendente", "Confirmado", "Cancelado")
) {
    // Adicione métodos adicionais, se necessário
}