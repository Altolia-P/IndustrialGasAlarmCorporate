package com.niit.industrialgasalarmcorporate.application.ai.service.impl;

import com.niit.industrialgasalarmcorporate.application.ai.dto.SendMessageDTO;
import com.niit.industrialgasalarmcorporate.application.ai.service.AIChatService;
import com.niit.industrialgasalarmcorporate.application.ai.vo.ChatResponseVO;
import com.niit.industrialgasalarmcorporate.assembler.AIAssembler;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.content.Content;
import com.niit.industrialgasalarmcorporate.domain.content.ContentRepository;
import com.niit.industrialgasalarmcorporate.domain.product.Product;
import com.niit.industrialgasalarmcorporate.domain.product.ProductRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.ai.config.DeepSeekConfig;
import com.niit.industrialgasalarmcorporate.infrastructure.ai.search.EmbeddingSearchService;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.AIChatRateLimitRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.ChatSessionRepository;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIChatServiceImpl implements AIChatService {

    private static final int MAX_RESULTS = 3;
    private static final int DESCRIPTION_MAX_LEN = 100;
    private static final String SYSTEM_PROMPT = """
            你是工业气体报警企业的智能助手，负责帮助用户了解我们的产品（气体检测仪、报警器等）和解决方案。

            规则：
            1. 用中文回答，简洁专业，200字以内。
            2. 如果用户询问产品或需要推荐，结合下方【匹配产品】给出推荐。
            3. 如果用户询问解决方案，结合下方【匹配方案】给出推荐。
            4. 如果用户的问题与工业气体安全无关，友好引导用户说明需求。
            5. 回答中提及产品时使用产品全名，方便用户了解。
            """;

    private static final ExecutorService AI_EXECUTOR = Executors.newFixedThreadPool(4, r -> {
        Thread t = new Thread(r, "ai-chat-worker");
        t.setDaemon(true);
        return t;
    });

    private final OpenAiChatModel chatModel;
    private final DeepSeekConfig deepSeekConfig;
    private final ProductRepository productRepository;
    private final ContentRepository contentRepository;
    private final AIChatRateLimitRepository rateLimitRepository;
    private final ChatSessionRepository sessionRepository;
    private final Optional<EmbeddingSearchService> embeddingSearch;

    @Override
    public ChatResponseVO chat(SendMessageDTO dto, String clientIp) {
        if (!rateLimitRepository.tryAcquire(clientIp)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "提问过于频繁，请稍后重试");
        }

        String sessionId = dto.getSessionId() != null && !dto.getSessionId().isBlank()
                ? dto.getSessionId()
                : UUID.randomUUID().toString();

        List<Product> matchedProducts = searchProducts(dto.getMessage());
        List<Content> matchedSolutions = searchSolutions(dto.getMessage());

        List<ChatMessage> history = sessionRepository.getMessages(sessionId);
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(SystemMessage.from(SYSTEM_PROMPT + buildContextPrompt(matchedProducts, matchedSolutions)));
        messages.addAll(history);
        messages.add(UserMessage.from(dto.getMessage()));

        String reply;
        if (!deepSeekConfig.isChatConfigured()) {
            reply = "AI 服务未配置 API Key，请联系管理员在 application-local.yml 中配置 deepseek.api-key。";
        } else {
            try {
                reply = CompletableFuture
                        .supplyAsync(() -> chatModel.chat(messages.toArray(new ChatMessage[0])).aiMessage().text(), AI_EXECUTOR)
                        .get(15, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                log.warn("DeepSeek API 调用超时: sessionId={}", sessionId);
                reply = "抱歉，AI 服务响应超时，请稍后重试。您也可以直接浏览我们的产品和解决方案页面。";
            } catch (Exception e) {
                log.warn("DeepSeek API 调用失败: sessionId={}", sessionId, e);
                reply = "抱歉，AI 服务暂时不可用，请稍后重试。您也可以直接浏览我们的产品和解决方案页面。";
            }
        }

        sessionRepository.addMessages(
                sessionId,
                UserMessage.from(dto.getMessage()),
                dev.langchain4j.data.message.AiMessage.from(reply)
        );

        ChatResponseVO vo = new ChatResponseVO();
        vo.setSessionId(sessionId);
        vo.setReply(reply);
        if (!matchedProducts.isEmpty()) {
            vo.setRecommendedProducts(matchedProducts.stream().map(AIAssembler::toProductVO).toList());
        }
        if (!matchedSolutions.isEmpty()) {
            vo.setRecommendedSolutions(matchedSolutions.stream().map(AIAssembler::toSolutionVO).toList());
        }
        return vo;
    }

    private List<Product> searchProducts(String query) {
        if (embeddingSearch.isPresent()) {
            try {
                List<String> ids = embeddingSearch.get().searchProductIds(query, MAX_RESULTS);
                if (!ids.isEmpty()) {
                    List<Product> products = ids.stream()
                            .map(productRepository::findById)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList();
                    if (!products.isEmpty()) {
                        return products;
                    }
                }
            } catch (Exception e) {
                log.warn("向量检索产品失败，回退到关键字搜索", e);
            }
        }
        return productRepository.searchByKeyword(query, MAX_RESULTS);
    }

    private List<Content> searchSolutions(String query) {
        if (embeddingSearch.isPresent()) {
            try {
                List<String> ids = embeddingSearch.get().searchSolutionIds(query, MAX_RESULTS);
                if (!ids.isEmpty()) {
                    List<Content> solutions = ids.stream()
                            .map(contentRepository::findById)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList();
                    if (!solutions.isEmpty()) {
                        return solutions;
                    }
                }
            } catch (Exception e) {
                log.warn("向量检索方案失败，回退到关键字搜索", e);
            }
        }
        return contentRepository.searchByKeyword(query, MAX_RESULTS);
    }

    private String buildContextPrompt(List<Product> products, List<Content> solutions) {
        StringBuilder sb = new StringBuilder();
        if (!products.isEmpty()) {
            sb.append("\n\n【匹配产品】\n");
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                sb.append(i + 1).append(". ").append(p.getName())
                        .append("：").append(truncate(p.getDescription(), DESCRIPTION_MAX_LEN)).append("\n");
            }
        }
        if (!solutions.isEmpty()) {
            sb.append("\n【匹配方案】\n");
            for (int i = 0; i < solutions.size(); i++) {
                Content s = solutions.get(i);
                sb.append(i + 1).append(". ").append(s.getTitle())
                        .append("：").append(truncate(s.getSummary(), DESCRIPTION_MAX_LEN)).append("\n");
            }
        }
        if (sb.length() == 0) {
            sb.append("\n\n当前没有匹配到具体产品或方案，请根据用户需求给出通用建议。");
        }
        return sb.toString();
    }

    private static String truncate(String text, int maxLen) {
        if (text == null || text.length() <= maxLen) {
            return text;
        }
        return text.substring(0, maxLen) + "…";
    }
}
