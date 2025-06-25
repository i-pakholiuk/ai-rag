package my.aitest.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import my.aitest.config.AiRagProperties;
import my.aitest.model.ChunkModel;
import my.aitest.model.JsonDocument;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmbeddingsService {

  @Autowired
  private EmbeddingModel embeddingModel;
  @Autowired
  private EmbeddingStore<TextSegment> vectorStore;
  @Autowired
  private DocumentSplitter documentSplitter;
  @Autowired
  private AiRagProperties aiRagProperties;

  // Build and store embeddings from a list of json documents
  public void build(List<JsonDocument> docs) {
    log.info("Starting embeddings creation: {} documents", docs.size());

    int storedDocs = 0;
    for (JsonDocument doc : docs) {
      try {
        // splitting the document
        List<TextSegment> segments = documentSplitter.split(
            Document.from(
                createContent(doc),
                Metadata.from(createMetadata(doc)
            )));

        // create embeddings
        List<Embedding> embedding = embeddingModel.embedAll(segments).content();
        // save embeddings
        vectorStore.addAll(embedding, segments);

        log.debug("Stored document: {} ({} segments)", doc.getId(), segments.size());

        storedDocs++;
      } catch (Exception e) {
        log.error("Error storing document {}: {}", doc.getId(), e.getMessage());
      }

      if (storedDocs % 100 == 0) {
        log.info("Stored {}% of documents", 100 * storedDocs / docs.size());
      }
    }

    log.info("Processed and stored: {} documents", storedDocs);

  }

  // Build and store embeddings from a list of json documents.
  // Batch version with chunking
  public void buildBatch(List<JsonDocument> docs) {
    log.info("Starting embeddings creation: {} documents", docs.size());

    List<List<JsonDocument>> lists = chunk(docs, aiRagProperties.getBatchSize());

    AtomicInteger counter = new AtomicInteger(0);
    // parallel chunk processing
    List<ChunkModel> list = lists.stream()
        .parallel()
        .map(documents -> {
          ChunkModel c = buildChunk(documents);
          counter.addAndGet(documents.size());
          log.info("Docs processed {} of {}", counter.get(), docs.size());
          return c;
        }).toList();

    AtomicInteger counter2 = new AtomicInteger(0);
    // chunkend embeddings saving
    list.forEach(c -> {
      counter2.getAndIncrement();
      log.info("Stored chunks {} of {}", counter2.get(), list.size());
      vectorStore.addAll(c.getEmbeddings(), c.getSegments());
    });

    log.info("Processed and stored: {} documents in {} chunks", counter, counter2);

  }

  private Map<String, Object> createMetadata(JsonDocument artwork) {
    Map<String, Object> metadata = new HashMap<>();
    if (artwork.getId() != null) metadata.put("id", artwork.getId());
    if (artwork.getAccessionNumber() != null) metadata.put("accession_number", artwork.getAccessionNumber());
    if (artwork.getClassification() != null) metadata.put("classification", artwork.getClassification());
    if (artwork.getObjectName() != null) metadata.put("object_name", artwork.getObjectName());
    if (artwork.getDepartment() != null) metadata.put("department", artwork.getDepartment());
    if (artwork.getRightsType() != null) metadata.put("rights_type", artwork.getRightsType());
    if (artwork.getRoom() != null) metadata.put("room", artwork.getRoom());
    if (artwork.getImage() != null) metadata.put("image", artwork.getImage());
    if (artwork.getImageWidth() != null) metadata.put("image_width", artwork.getImageWidth());
    if (artwork.getImageHeight() != null) metadata.put("image_height", artwork.getImageHeight());
    if (artwork.getCuratorApproved() != null) metadata.put("curator_approved", artwork.getCuratorApproved());
    if (artwork.getRestricted() != null) metadata.put("restricted", artwork.getRestricted());

    return metadata;
  }
  private String createContent(JsonDocument artwork) {
    return
        (Strings.isBlank(artwork.getTitle()) ? "" : "Title: " + artwork.getTitle() + "\n") +
            (Strings.isBlank(artwork.getText()) ? "" : "Text: " + artwork.getText() + "\n") +
            (Strings.isBlank(artwork.getDescription()) ? "" : "Description: " + artwork.getDescription() + "\n") +
            (Strings.isBlank(artwork.getArtist()) ? "" : "Artist: " + artwork.getArtist() + "\n") +
            (Strings.isBlank(artwork.getLifeDate()) ? "" : "Life Dates: " + artwork.getLifeDate() + "\n") +
            (Strings.isBlank(artwork.getNationality()) ? "" : "Nationality: " + artwork.getNationality() + "\n") +
            (Strings.isBlank(artwork.getCountry()) ? "" : "Country: " + artwork.getCountry() + "\n") +
            (Strings.isBlank(artwork.getCountry()) ? "" : "Сontinent: " + artwork.getContinent() + "\n") +
            (Strings.isBlank(artwork.getDated()) ? "" : "Dated: " + artwork.getDated() + "\n") +
            (Strings.isBlank(artwork.getMedium()) ? "" : "Medium: " + artwork.getMedium() + "\n") +
            (Strings.isBlank(artwork.getDimension()) ? "" : "Dimensions: " + artwork.getDimension() + "\n") +
            (Strings.isBlank(artwork.getSigned()) ? "" : "Signed: " + artwork.getSigned() + "\n") +
            (Strings.isBlank(artwork.getSigned()) ? "" : "Life date: " + artwork.getLifeDate() + "\n");
  }

  private ChunkModel buildChunk(List<JsonDocument> docs) {
    List<TextSegment> segments = new LinkedList<>();

    for (JsonDocument doc : docs) {
      try {
        // splitting the documents
        segments.addAll(documentSplitter.split(
            Document.from(
                createContent(doc),
                Metadata.from(createMetadata(doc)))));
      } catch (Exception ex) {
        log.error("Chunking error", ex);
      }
    }

    try {
      List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

      return ChunkModel.builder()
          .segments(segments)
          .embeddings(embeddings)
          .build();
    } catch (Exception ex) {
        log.error("Embeddings creation error", ex);

        return ChunkModel.builder()
            .segments(List.of())
            .embeddings(List.of())
            .build();
    }

  }

  public static <T> List<List<T>> chunk(List<T> list, int chunkSize) {
    AtomicInteger counter = new AtomicInteger(0);

    return new ArrayList<>(list.stream()
        .collect(Collectors.groupingBy(
            item -> counter.getAndIncrement() / chunkSize,
            Collectors.toList()
        ))
        .values());
  }

}
