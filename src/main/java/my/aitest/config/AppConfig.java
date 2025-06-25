package my.aitest.config;

import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import java.time.Duration;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Autowired
  private VectorStoreProperties vectorStoreProperties;
  @Autowired
  private AiRagProperties aiRagProperties;

  @Bean
  public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
    return chatClientBuilder.build();
  }

  @Bean
  public EmbeddingModel embeddingModel() {
    return new AllMiniLmL6V2EmbeddingModel();
  }

  @Bean
  public EmbeddingStore<TextSegment> vectorStore() {
    return ChromaEmbeddingStore.builder()
        .collectionName(vectorStoreProperties.getCollectionName())
        .baseUrl(vectorStoreProperties.getBaseUrl() + ":" + vectorStoreProperties.getPort())
        .timeout(Duration.ofSeconds(vectorStoreProperties.getTimeout()))
        .build();
  }
  @Bean
  public EmbeddingStoreContentRetriever contentRetriever (EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> vectorStore) {
    return EmbeddingStoreContentRetriever.builder()
        .embeddingStore(vectorStore)
        .embeddingModel(embeddingModel)
        .maxResults(aiRagProperties.getVectorMaxResults())
        .minScore(aiRagProperties.getVectorMinSimilarity())
        .build();
  }

  @Bean
  public DocumentSplitter documentSplitter() {
    return DocumentSplitters.recursive(aiRagProperties.getMaxSegmentSize(), aiRagProperties.getMaxOverlapSize());
  }

}
