package com.tads.airport_system.ms_func.controller

import com.tads.airport_system.ms_func.dto.FuncDTO
import com.tads.airport_system.ms_func.model.Func
import com.tads.airport_system.ms_func.service.FuncService

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/func")

class FuncController(
    private val funcService: FuncService
) 
    @PostMapping("/create")
    fun createFunc(@RequestBody funcDTO: FuncDTO): ResponseEntity<FuncDTO> {
        val createdFunc = funcService.createFunc(funcDTO)
        return ResponseEntity.ok(createdFunc)
    }

    @GetMapping("/{id}")
    fun getFunc(@PathVariable id: Long): ResponseEntity<FuncDTO> {
        val func = funcService.getFunc(id)
        return if (func != null) {
            ResponseEntity.ok(func)
        } else {
            ResponseEntity.notFound().build()
        }
    }