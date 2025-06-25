package my.aitest.service;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromptService {

  @Autowired
  private PromptTemplateProvider promptTemplateProvider;

  public String buildPrompt(String template, Map<String, Object> variables) {
    String templateBody = promptTemplateProvider.loadTemplate(template);
    return replacePlaceholders(templateBody, variables);
  }

  private String replacePlaceholders(String body, Map<String, Object> variables) {
    String result = body;
    for (Map.Entry<String, Object> entry : variables.entrySet()) {
      result = result.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
    }
    return result;
  }
}
