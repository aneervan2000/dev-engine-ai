package com.anee.projects.lovable_clone.repository;

import com.anee.projects.lovable_clone.entities.ChatSession;
import com.anee.projects.lovable_clone.entities.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {
}
