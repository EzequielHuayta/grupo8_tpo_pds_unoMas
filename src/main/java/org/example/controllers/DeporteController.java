package org.example.controllers;

import org.example.model.Deporte;
import org.example.service.DeporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deportes")
@CrossOrigin(origins = "*")
public class DeporteController {

    private final DeporteService deporteService;

    public DeporteController(DeporteService deporteService) {
        this.deporteService = deporteService;
    }

    @GetMapping
    public ResponseEntity<List<Deporte>> listarDeportes() {
        return ResponseEntity.ok(deporteService.getDeportes());
    }
}
