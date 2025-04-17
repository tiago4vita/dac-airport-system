package com.tads.airport_system.ms_func.model

import jakarta.persistence.*

@Entity
@Table(name = "func")
data class Func(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val userId: Long, // ID do Usuário no microsserviço auth

    @Column(nullable = false)
    val role: String, // Cargo do funcionário

    @Column(nullable = false)
    val department: String // Departamento do funcionário
)