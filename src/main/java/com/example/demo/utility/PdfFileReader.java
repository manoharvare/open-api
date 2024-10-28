package com.example.demo.utility;

import com.example.demo.config.Config;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class PdfFileReader {
    private final VectorStore vectorStore;

    private final Config config;

    private final MongoTemplate mongoTemplate;
    @Value("classpath:*****.pdf")
    private Resource pdfResource;

    public PdfFileReader(VectorStore vectorStore, Config config, MongoTemplate mongoTemplate) {
        this.vectorStore = vectorStore;
        this.config = config;
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void init() {
        PdfDocumentReaderConfig pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder().withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder().build()).build();
        var pdfReader = new PagePdfDocumentReader(pdfResource, pdfDocumentReaderConfig);
        var textSplitter = new TokenTextSplitter();
        VectorStore vectorStore = config.mongodbVectorStore(mongoTemplate, config.embeddingModel(), "demo_collection");
        vectorStore.add(textSplitter.apply(pdfReader.get()));

    }
}
