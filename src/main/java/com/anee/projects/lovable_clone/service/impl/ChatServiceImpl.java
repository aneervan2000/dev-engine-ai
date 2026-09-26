package com.anee.projects.lovable_clone.service.impl;

import com.anee.projects.lovable_clone.dto.chat.ChatResponse;
import com.anee.projects.lovable_clone.entities.ChatMessage;
import com.anee.projects.lovable_clone.entities.ChatSession;
import com.anee.projects.lovable_clone.entities.ChatSessionId;
import com.anee.projects.lovable_clone.mapper.ChatMapper;
import com.anee.projects.lovable_clone.repository.ChatMessageRepository;
import com.anee.projects.lovable_clone.repository.ChatSessionRepository;
import com.anee.projects.lovable_clone.security.AuthUtil;
import com.anee.projects.lovable_clone.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final AuthUtil authUtil;
    private final ChatMapper chatMapper;

    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {
        Long userId = authUtil.getCurrentUserId();

        ChatSession chatSession = chatSessionRepository.getReferenceById(
                new ChatSessionId(projectId, userId)
        );

        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatSession(chatSession);

        return chatMapper.fromListOfChatMessage(chatMessageList);
    }
}
