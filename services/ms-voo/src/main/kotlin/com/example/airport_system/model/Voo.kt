package com.tads.airport_system.ms_voo.model
import jakarta.persistence.*
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
    val origem: String,
    val destino: String,
    val dataHora: String,
    val preco: Double,
    val qtdPoltronas: Int = 0,
    val status: String = "Confirmado" // Status do voo (ex: "Pendente", "Confirmado", "Cancelado")
) {
    // Adicione métodos adicionais, se necessário
}