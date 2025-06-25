package my.aitest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "vectorstore.chroma")
public class VectorStoreProperties {
  private String collectionName;
  private String baseUrl;
  private Integer port;
  private Integer timeout;
}
