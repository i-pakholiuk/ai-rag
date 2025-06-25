package my.aitest.service;

import lombok.extern.slf4j.Slf4j;
import my.aitest.model.ChatRequest;
import my.aitest.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CachedChatService {

  @Autowired
  private ChatService chatService;
  @Autowired
  private CacheManager cacheManager;

  public ChatResponse prompt(ChatRequest request, boolean useCache) {
    log.info("Caching option: {}", useCache);

    ChatResponse response;
    if (useCache) {
      response = cacheManager.getCache("prompts").get(request, ChatResponse.class);
      if (response != null) {
        log.info("Used cached answer: {}", response.getAnswer());
        return response;
      }
    }

    response = chatService.prompt(request);
    cacheManager.getCache("prompts").put(request, response);

    return  response;
  }

}
