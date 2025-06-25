package my.aitest.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import my.aitest.config.AiRagProperties;
import my.aitest.error.AppException;
import my.aitest.model.JsonDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DocumentService {

  @Autowired
  private AiRagProperties aiRagProperties;

  private static final ObjectMapper MAPPER = new ObjectMapper()
      .findAndRegisterModules()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  public List<JsonDocument> loadJsonDocuments() {

    log.info("Starting loading of json documents");

    Resource[] resources;
    try {
      PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
      resources = resolver.getResources(aiRagProperties.getDocStoragePath());
    } catch (IOException ex) {
      throw new AppException(ex);
    }

    log.info("Found: {} resources", resources.length);

    List<JsonDocument> documents = Arrays.stream(resources)
        .parallel()
        .map(this::loadJsonDocumentsFromResource)
        .filter(Objects::nonNull)
        .toList();

    log.info("Loaded: {} documents", documents.size());

    return documents;
  }

  private JsonDocument loadJsonDocumentsFromResource(Resource resource) {
    try {
      log.trace("Starting loading documents from the resource {}", resource.getFilename());

      JsonDocument document = MAPPER.readValue(resource.getFile(), JsonDocument.class);

      log.trace("Loaded document {}", resource.getFilename());

      return document;
    } catch (Exception ex) {
      log.error("Error loading documents from the resource {}", resource.getFilename());
      return null;
    }
  }

}
