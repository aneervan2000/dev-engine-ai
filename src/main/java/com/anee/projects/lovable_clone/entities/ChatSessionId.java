package com.anee.projects.lovable_clone.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ChatSessionId implements Serializable {
    Long projectId;
    Long userId;
}
