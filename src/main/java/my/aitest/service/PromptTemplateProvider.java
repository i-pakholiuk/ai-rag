package my.aitest.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import my.aitest.error.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class PromptTemplateProvider {

  @Autowired
  private ResourceLoader resourceLoader;

  @Cacheable("templates")
  public String loadTemplate(String template) {
    return loadTemplateFromFile(template);
  }

  private String loadTemplateFromFile(String template) {
    try {
      Resource resource = resourceLoader.getResource("classpath:prompts/" + template + ".txt");
      return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new AppException("Failed to load template: " + template, e);
    }
  }

}
