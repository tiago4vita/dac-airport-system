package com.tads.airport_system.ms_auth.controller

import com.tads.airport_system.ms_auth.dto.LoginDTO
import com.tads.airport_system.ms_auth.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@RequestBody loginDTO: LoginDTO): ResponseEntity<Any> {
        val isAuthenticated = authService.authenticate(loginDTO.login, loginDTO.senha)
        
        return if (isAuthenticated) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.status(401).build()
        }
    }
    
    // Test endpoint to verify controller is working
    @GetMapping("/test")
    fun test(): ResponseEntity<String> {
        return ResponseEntity.ok("Auth service is working")
    }
    
    /*
    @GetMapping("/{id}")
    fun getUser(@PathVariable id:Long){

    }

    @GetMapping
    fun getAllUsers(){

    }

    @PostMapping
    fun createUser(@RequestBody userRequest : UserRequest){

    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id:Long,@RequestBody userRequest : UserRequest){

    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id:Long){

    }
    */
}