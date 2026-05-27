package com.niit.industrialgasalarmcorporate.infrastructure.redis;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!test")
public class ChatSessionRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SESSION_PREFIX = "ai:session:";
    private static final int MAX_HISTORY = 10;
    private static final int SESSION_TTL_HOURS = 24;

    public List<ChatMessage> getMessages(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        @SuppressWarnings("unchecked")
        List<ChatMessageEntry> entries = (List<ChatMessageEntry>) redisTemplate.opsForValue().get(key);
        if (entries == null) {
            return new ArrayList<>();
        }
        return entries.stream().map(ChatMessageEntry::toChatMessage).toList();
    }

    public void addMessages(String sessionId, ChatMessage userMsg, ChatMessage aiMsg) {
        String key = SESSION_PREFIX + sessionId;
        List<ChatMessageEntry> entries = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<ChatMessageEntry> existing = (List<ChatMessageEntry>) redisTemplate.opsForValue().get(key);
        if (existing != null) {
            entries.addAll(existing);
        }
        entries.add(ChatMessageEntry.from(userMsg));
        entries.add(ChatMessageEntry.from(aiMsg));
        if (entries.size() > MAX_HISTORY) {
            entries = new ArrayList<>(entries.subList(entries.size() - MAX_HISTORY, entries.size()));
        }
        redisTemplate.opsForValue().set(key, entries, SESSION_TTL_HOURS, TimeUnit.HOURS);
    }

    public record ChatMessageEntry(String type, String text) implements Serializable {
        static ChatMessageEntry from(ChatMessage msg) {
            String text;
            if (msg instanceof UserMessage m) {
                text = m.singleText();
            } else if (msg instanceof AiMessage m) {
                text = m.text();
            } else if (msg instanceof SystemMessage m) {
                text = m.text();
            } else {
                text = "";
            }
            return new ChatMessageEntry(msg.type().name(), text);
        }

        ChatMessage toChatMessage() {
            return switch (type) {
                case "SYSTEM" -> SystemMessage.from(text);
                case "USER" -> UserMessage.from(text);
                case "AI" -> AiMessage.from(text);
                default -> UserMessage.from(text);
            };
        }
    }
}
