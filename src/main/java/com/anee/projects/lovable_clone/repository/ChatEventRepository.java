package com.anee.projects.lovable_clone.repository;

import com.anee.projects.lovable_clone.entities.ChatEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatEventRepository extends JpaRepository<ChatEvent, Long> {
}
