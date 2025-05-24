//verify package if is correct, need to create if don't exist
package com.tads.airport_system.ms_voo.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class VooDTO(
    val codigo: String,
    val data: LocalDateTime,
    val valorPassagem: BigDecimal,
    val aeroportoOrigem: AeroportoDTO,
    val aeroportoDestino: AeroportoDTO
) {
    // Métodos de validação
    fun isMesmoDia(outro: VooDTO): Boolean {
        return this.data.toLocalDate().isEqual(outro.data.toLocalDate())
    }

    fun horarioFormatado(): String {
        return "${data.hour.toString().padStart(2, '0')}:${data.minute.toString().padStart(2, '0')}"
    }

    //fix currency func
    fun toCurrencyString(): String {
        return "R$ ${valorPassagem.setScale(2)}"
    }
}

//create another dtoclass (?)
data class AeroportoDTO(
    val codigo: String,
    val nome: String,
    val cidade: String,
    val estado: String,
    val pais: String
)

//teste
fun simularListaDeVoos(): List<VooDTO> {
    val aeroportoOrigem = AeroportoDTO("GRU", "Guarulhos", "São Paulo", "SP", "Brasil")
    val aeroportoDestino = AeroportoDTO("GIG", "Galeão", "Rio de Janeiro", "RJ", "Brasil")
    
    val voos = mutableListOf<VooDTO>()
    
    for (i in 0 until 10) {
        val voo = VooDTO(
            codigo = "VOO$i",
            data = LocalDateTime.now().plusDays(i.toLong()),
            valorPassagem = BigDecimal(400 + (i * 50)),
            aeroportoOrigem = aeroportoOrigem,
            aeroportoDestino = aeroportoDestino
        )
        voos.add(voo)
    }

    voos.forEach {
        println("Voo ${it.codigo} - ${it.horarioFormatado()} - ${it.toCurrencyString()}")
    }

    val voosSomenteSegunda = voos.filter {
        it.data.dayOfWeek.value == 1
    }

    println("Voos que partem na segunda-feira:")
    voosSomenteSegunda.forEach {
        println(it)
    }

    val voosBaratos = voos.filter {
        it.valorPassagem < BigDecimal(600)
    }

    println("Voos com valor abaixo de R$600:")
    voosBaratos.forEach {
        println("${it.codigo} -> ${it.toCurrencyString()}")
    }

    // Agrupamento por dia
    val agrupadoPorData = voos.groupBy { it.data.toLocalDate() }

    agrupadoPorData.forEach { (data, listaVoos) ->
        println("Data: $data")
        listaVoos.forEach { println(" - ${it.codigo}") }
    }

    return voos
}
