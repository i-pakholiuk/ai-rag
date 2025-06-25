package my.aitest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import my.aitest.model.ChatRequest;
import my.aitest.model.ChatResponse;
import my.aitest.service.CachedChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@Tag(name = "Chat controller", description = "APIs to chat with AI model")
public class ChatController {

  @Autowired
  private CachedChatService chatService;

  @Operation(
      summary = "Ask a question to AI model",
      description = "It uses vector storage for contextualization and LLM for data processing and response preparation")
  @PostMapping("/prompt")
  public ChatResponse prompt(@RequestBody @Valid ChatRequest request,
      @RequestHeader(value = "Cache-Control", required = false) String cacheControl) {

    boolean useCache = cacheControl == null || !cacheControl.toLowerCase().contains("no-cache");

    return chatService.prompt(request, useCache);
  }
}
