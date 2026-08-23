package com.sginventario.inventarioWS.controller;

import com.sginventario.inventarioWS.dto.MarcaDTO;
import com.sginventario.inventarioWS.service.imp.MarcaServiceImpl;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sginventario.inventarioWS.exception.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/marcas")
public class MarcaController {

    @Autowired
    private MarcaServiceImpl service;

    @GetMapping
    public ApiResponse<List<MarcaDTO>> listar() {
        return new ApiResponse<>(true, "Listado", service.listar());
    }

    @GetMapping("/activos")
    public ApiResponse<List<MarcaDTO>> listarActivos() {
        return new ApiResponse<>(true, "Listado", service.listarActivos());
    }

    @GetMapping("/{id}")
    public ApiResponse<MarcaDTO> obtener(@PathVariable Integer id) {
        return new ApiResponse<>(true, "Marca encontrada", service.obtenerPorId(id));
    }

    @PostMapping
    public ApiResponse<MarcaDTO> guardar(@Valid @RequestBody MarcaDTO marca) {
        return new ApiResponse<>(true, "Marca creada", service.guardar(marca));
    }

    @PutMapping("/{id}")
    public ApiResponse<MarcaDTO> actualizar(@PathVariable Integer id,
            @Valid @RequestBody MarcaDTO marca) {
        marca.setId(id);
        return new ApiResponse<>(true, "Marca actualizada", service.actualizar(id, marca));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return new ApiResponse<>(true, "Marca eliminada", null);
    }
}
