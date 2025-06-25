package my.aitest.service;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import my.aitest.error.AppException;
import my.aitest.model.ChatRequest;
import my.aitest.model.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
@Slf4j
public class ChatService {

  @Autowired
  private ChatClient chatClient;
  @Autowired
  private PromptService promptService;
  @Autowired
  private EmbeddingStoreContentRetriever contentRetriever;

  private final static String PROMPT_TEMPLATE = "main-template";

  public ChatResponse prompt(ChatRequest request) {

    log.info("Answering the question: {}", request.getQuestion());

    // retrieve data from the vector storage
    List<TextSegment> relevantSegments = contentRetriever
        .retrieve(Query.from(request.getQuestion()))
        .stream()
        .map(Content::textSegment)
        .toList();

    if (relevantSegments.isEmpty()) {
      log.info("Relevant documents not found");

      return ChatResponse.builder()
          .answer("Relevant documents not found")
          .build();
    }

    // context and sources
    StringBuilder context = new StringBuilder();
    List<String> sources = new LinkedList<>();
    for (int i = 0; i < relevantSegments.size(); i++) {
      String docData = (i + 1) + ". " + relevantSegments.get(i).text();
      sources.add(docData);
      context.append(docData).append("\n");
    }

    Map<String, Object> params = Map.of("question", request.getQuestion(),"context", context.toString());
    String prompt = promptService.buildPrompt(PROMPT_TEMPLATE, params);
    log.debug("Prompt:\n {}", prompt);

    try {
      String answer = chatClient.prompt(prompt).call().content();
      log.info("Answer:\n {}", answer);

      return ChatResponse.builder()
          .answer(answer)
          .sources(sources)
          .build();
    } catch (Exception ex) {
      throw new AppException("Chat answering error", ex);
    }
  }
}