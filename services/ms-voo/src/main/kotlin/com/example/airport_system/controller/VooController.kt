package com.tads.airport_system.ms_voo.controller

//import com.tads.airport_system.ms_voo.dto.LoginDTO
import com.tads.airport_system.ms_voo.service.VooService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/voo")
class VooController(
    //private val authService: AuthService
) {
    @GetMapping("/test")
    fun test(): ResponseEntity<String> {
        return ResponseEntity.ok("Voo service is working")
    }

    // Test endpoint to verify controller is working
    @GetMapping("/test2")
    fun test2(): ResponseEntity<String> {
        return ResponseEntity.ok("Voo service is working 2")
    }

    @PostMapping("/create")
    fun createVoo(@RequestBody vooDTO: VooDTO): ResponseEntity<VooDTO> {
         val createdVoo = vooService.createVoo(funcDTO)
        return ResponseEntity.ok(createdVoo)
    }

}