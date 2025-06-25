package my.aitest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.rag")
public class AiRagProperties {
  private Integer vectorMaxResults;
  private Double vectorMinSimilarity;
  private Integer maxSegmentSize;
  private Integer maxOverlapSize;
  private String docStoragePath;
  private Integer batchSize;
}
