package my.aitest.model;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChunkModel {
  private List<TextSegment> segments;
  private List<Embedding> embeddings;
}
