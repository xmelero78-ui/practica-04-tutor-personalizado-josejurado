package com.springai.init.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.springai.init.model.Resultado;
import com.springai.init.service.TutorialesService;

@Service
public class TutorialesServiceImpl implements TutorialesService {
	private VectorStore vectorStore;
	private ChatClient chatClient;

	public TutorialesServiceImpl(VectorStore vectorStore, ChatClient chatClient) {
		this.vectorStore = vectorStore;
		this.chatClient = chatClient;
	}

	public List<Resultado> obtenerResultados(String mensajeUser) {

		List<Document> chunks = vectorStore
				.similaritySearch(SearchRequest.builder().query(mensajeUser).topK(15).build());
		
				
		Set<String> ficherosReales = chunks.stream()
				.map(d -> (String) d.getMetadata().getOrDefault("nombreFichero", "desconocida"))
				.collect(Collectors.toSet());

	
		String contexto = chunks.stream()
				.map(d -> "<documento nombre=" + d.getMetadata().getOrDefault("nombreFichero", "desconocida") + ">"	+ d.getText() + "</documento>")
				.collect(Collectors.joining("\n\n"));
		

		Prompt prompt = buildPrompt(mensajeUser, contexto);
		
	
		Resultado[] resultadosRaw = chatClient.prompt(prompt).call().entity(Resultado[].class);

		if (resultadosRaw == null) {
			return List.of();
		}

		List<Resultado> resultadosValidados = Arrays.stream(resultadosRaw)
				.filter(r -> ficherosReales.contains(r.getNombreFichero())).collect(Collectors.toList());

		return resultadosValidados;
	}

	private Prompt buildPrompt(String mensajeUser, String contexto) {
		String systemText = "Eres un asistente experto en recuperación de información y análisis de documentos técnicos.\n\n" +
	            "INSTRUCCIONES:\n" +
	            "1. Analiza los documentos proporcionados dentro de las etiquetas XML en relación con la consulta del usuario.\n" +
	            "2. Devuelve EXCLUSIVAMENTE un array JSON válido, sin bloques de código markdown (nada de ```json) y sin texto adicional fuera del JSON.\n" +
	            "3. Cada objeto del JSON debe tener exactamente estas dos propiedades:\n" +
	            "   - \"nombreFichero\": Copia exactamente el valor del atributo 'nombre' de la etiqueta XML del documento relevante.\n" +
	            "   - \"explicacion\": Escribe un breve resumen de 1 o 2 frases explicando qué contenido relevante sobre la consulta del usuario contiene este documento.\n" +
	            "4. Si ningún documento es relevante, devuelve un array vacío: [].\n\n" +
	            "Contexto:\n" + contexto;
		//IMPORTANTE: No usar PromptTemplate porque el contexto tiene codigo java que no se interpreta bien como texto y produce fallos en la carga del contexto.
		//PromptTemplate templateSys = new PromptTemplate(systemText);
		//Message systemMessage = templateSys.createMessage(templateSys);
		Message systemMessage = new SystemMessage(systemText);
		Message userMessage = new UserMessage("Busca los ficheros relacionados con esta consulta: " + mensajeUser);
		return new Prompt(List.of(systemMessage, userMessage));

	}
}
