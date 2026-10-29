package com.springai.init.component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
public class PdfLoaderComponent {
	private final TokenTextSplitter splitter;

	public PdfLoaderComponent() {
    	this.splitter = TokenTextSplitter.builder()
                .withChunkSize(500)             // 500 tokens máximos por bloque
                .withMaxNumChunks(1000)        // Límite máximo de fragmentos totales
                .build();
	}

	public List<Document> obtenerChunks() {

		try {

			Resource[] pdfs = obtenerPDFs();

			return splitter.split(Arrays.stream(pdfs).map(pdf -> {
				try {
					return new Document(extraerTexto(pdf), Map.of("nombreFichero", pdf.getFilename()));
				} catch (IOException e) {
					throw new RuntimeException("Error leyendo PDF: " + pdf.getFilename(), e);
				}
			}).toList());

		} catch (IOException e) {
			throw new RuntimeException("Error leyendo PDFs", e);
		}

	}

	private Resource[] obtenerPDFs() throws IOException {
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

		return resolver.getResources("classpath:/static/*.pdf");
	}

	private String extraerTexto(Resource resource) throws IOException {
		try (PDDocument pdf = Loader.loadPDF(resource.getContentAsByteArray())) {
            return new PDFTextStripper().getText(pdf);
        }
	}


}
