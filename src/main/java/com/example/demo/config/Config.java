package com.example.demo.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.MongoDBAtlasVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@SpringBootConfiguration
@EnableAutoConfiguration
public class Config {

    @Value("${spring.ai.openai.api-key}")
    private String openAiKey;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${spring.ai.vectorstore.mongodb.indexName:vector_index}")
    private String indexName;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.ai.vectorstore.mongodb.initialize-schema}")
    private Boolean initSchema;

    @Bean
    public EmbeddingModel embeddingModel() {
        return new OpenAiEmbeddingModel(new OpenAiApi(openAiKey));
    }

//    @Bean
//    public MongoDBAtlasVectorStore.MongoDBVectorStoreConfig vectorStoreConfig() {
//        return MongoDBAtlasVectorStore.MongoDBVectorStoreConfig.builder()
//                .withVectorIndexName(indexName)
//                .build();
//    }

    /**
     * Creates a VectorStore bean with a dynamic collection name.
     */
    public VectorStore mongodbVectorStore(
            MongoTemplate mongoTemplate,
            EmbeddingModel embeddingModel,
            String dynamicCollectionName) {

        // Set custom collection name dynamically
        MongoDBAtlasVectorStore.MongoDBVectorStoreConfig config =
                MongoDBAtlasVectorStore.MongoDBVectorStoreConfig.builder()
                        .withCollectionName(dynamicCollectionName)
                        .withVectorIndexName("demo-index")
                        .build();

        return new MongoDBAtlasVectorStore(mongoTemplate, embeddingModel, config, initSchema);
    }
}
