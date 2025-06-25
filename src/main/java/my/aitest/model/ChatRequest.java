package my.aitest.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRequest {
  @NotBlank(message = "Question cannot be empty")
  private String question;
}
