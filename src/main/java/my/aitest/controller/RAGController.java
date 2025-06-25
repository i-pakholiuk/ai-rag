package my.aitest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import my.aitest.model.JsonDocument;
import my.aitest.service.DocumentService;
import my.aitest.service.EmbeddingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rag")
@Tag(name = "RAG controller", description = "APIs for RAG operations")
public class RAGController {

  @Autowired
  private DocumentService documentService;
  @Autowired
  private EmbeddingsService embeddingsService;

  @Operation(
      summary = "Create and store embeddings",
      description = "Load json documents, segment them and store embeddings in a vector db")
  @PostMapping("/embeddings")
  public String createEmbeddings() {
    // load json documents
    List<JsonDocument> documents = documentService.loadJsonDocuments();
    // build embeddings
    embeddingsService.build(documents);

    return "Ok";
  }

  @Operation(
      summary = "Create and store embeddings - batch mode (accelerated)",
      description = "Load json documents, segment them and store embeddings in a vector db")
  @PostMapping("/embeddings/batch")
  public String createEmbeddingsBatch() {
    // load json documents
    List<JsonDocument> documents = documentService.loadJsonDocuments();
    // build embeddings
    embeddingsService.buildBatch(documents);

    return "Ok";
  }

}
