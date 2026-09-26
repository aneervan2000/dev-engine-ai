package com.anee.projects.lovable_clone.dto.chat;

import com.anee.projects.lovable_clone.entities.ChatEvent;
import com.anee.projects.lovable_clone.entities.ChatSession;
import com.anee.projects.lovable_clone.enums.MessageRole;

import java.time.Instant;
import java.util.List;

public record ChatResponse (
    Long id,
    ChatSession chatSession,
    MessageRole role, // USER, ASSISTANT
    List<ChatEvent> events,
    String content,
    Integer tokenUsed,
    Instant createdAt) {

}
