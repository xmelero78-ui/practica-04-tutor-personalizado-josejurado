package com.springai.init.config;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.context.annotation.Configuration;

import com.springai.init.component.PdfLoaderComponent;

import io.qdrant.client.QdrantClient;

@Configuration
public class LoaderConfiguration {


	public LoaderConfiguration(
			PdfLoaderComponent pdfLoaderComponent, 
			QdrantClient qdrantClient, 
			EmbeddingModel embeddingModel, 
			QdrantVectorStore vectorStore) {
		
		this.loadVector(pdfLoaderComponent, qdrantClient, embeddingModel, vectorStore);
	}
	
	public void loadVector(
			PdfLoaderComponent pdfLoaderComponent, 
			QdrantClient qdrantClient, 
			EmbeddingModel embeddingModel, 
			QdrantVectorStore vectorStore) {

		String name = vectorStore.getName();		
		List<Document> listDocuments = pdfLoaderComponent.obtenerChunks();
		vectorStore.add(listDocuments);
	
	}
}
