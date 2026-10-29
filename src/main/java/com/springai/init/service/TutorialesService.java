package com.springai.init.service;

import java.util.List;

import com.springai.init.model.Resultado;

public interface TutorialesService {

	List<Resultado> obtenerResultados(String mensajeUser);
}
