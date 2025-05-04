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
) {
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

    @GetMapping("/all")
    fun getAllFuncs(): ResponseEntity<List<FuncDTO>> {
        val funcs = funcService.getAllFuncs()
        return ResponseEntity.ok(funcs)
    }

    @PutMapping("/{id}")
    fun updateFunc(@PathVariable id: Long, @RequestBody funcDTO: FuncDTO): ResponseEntity<FuncDTO> {
        val updatedFunc = funcService.updateFunc(id, funcDTO)
        return if (updatedFunc != null) {
            ResponseEntity.ok(updatedFunc)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    @DeleteMapping("/{id}")
    fun deleteFunc(@PathVariable id: Long): ResponseEntity<Void> {
        return if (funcService.deleteFunc(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
    @GetMapping("/search")
    fun searchFuncByName(@RequestParam name: String): ResponseEntity<List<FuncDTO>> {
        val funcs = funcService.findByName(name)
        return ResponseEntity.ok(funcs)
    }
    @GetMapping("/count")
    fun countFuncs(): ResponseEntity<Long> {
        val count = funcService.countFuncs()
        return ResponseEntity.ok(count)
    }
    @GetMapping("/department/{id}")
    fun getFuncByDepartment(@PathVariable id: Long): ResponseEntity<FuncDTO> {
        val func = funcService.getFuncByDepartment(id)
        return if (func != null) {
            ResponseEntity.ok(func)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}