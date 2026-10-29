package com.springai.init.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springai.init.model.Resultado;
import com.springai.init.service.TutorialesService;

@RestController
@RequestMapping("tutoriales")
public class TutorialesController {

	private TutorialesService tutorialesService;

	public TutorialesController(TutorialesService tutorialesService) {
		this.tutorialesService = tutorialesService;
	}

	@GetMapping("consultar")
	public ResponseEntity<List<Resultado>> resultados(@RequestParam String mensajeUser) {
		return ResponseEntity.ok(tutorialesService.obtenerResultados(mensajeUser));
	}
}
