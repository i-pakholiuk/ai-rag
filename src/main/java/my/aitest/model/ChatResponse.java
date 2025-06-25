package my.aitest.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ChatResponse {
  private String answer;
  private List<String> sources;
}
