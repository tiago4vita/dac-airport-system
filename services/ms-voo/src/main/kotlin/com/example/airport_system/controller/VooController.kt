package com.example.airport_system.ms_voo.controller

import com.example.airport_system.ms_voo.dto.VooDTO
import com.example.airport_system.ms_voo.service.VooService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/voo")
class VooController(
    private val vooService: VooService // Injected VooService
    //private val authService: AuthService
) {
    @GetMapping("/test")
    fun test(): ResponseEntity<String> {
        return ResponseEntity.ok("Voo service is working")
    }

    @GetMapping("/test2")
    fun test2(): ResponseEntity<String> {
        return ResponseEntity.ok("Voo service is working 2")
    }

    @PostMapping("/create")
    fun createVoo(@RequestBody vooDTO: VooDTO): ResponseEntity<VooDTO> {
        val createdVoo = vooService.createVoo(vooDTO)
        return ResponseEntity.ok(createdVoo)
    }
}