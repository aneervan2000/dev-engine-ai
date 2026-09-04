package com.anee.projects.lovable_clone.llm.advisors;

import com.anee.projects.lovable_clone.dto.project.FileNode;
import com.anee.projects.lovable_clone.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileTreeContextAdvisor implements StreamAdvisor {

    private final ProjectFileService projectFileService;

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain streamAdvisorChain) {
        Map<String, Object> context = request.context();
        Long projectId = Long.parseLong(context.getOrDefault("projectId", 0).toString());

        ChatClientRequest augmentedChatClientRequest = augmentRequestWithFileTree(request, projectId);

        return streamAdvisorChain.nextStream(augmentedChatClientRequest);
    }

    /**     * Augments the incoming chat request with the current project's file tree so the     * language model has repository context before producing a response.     *     * <p>This method preserves any existing system message from the original prompt,     * isolates the remaining non-system instructions, and then retrieves the project     * structure from {@link ProjectFileService}. The file tree is converted into a     * textual system-level context block and appended as an additional instruction so     * the downstream model can reason about the workspace layout while responding.</p>     *     * @param request the original chat request whose prompt should be enriched with     *                repository context     * @param projectId the identifier of the project whose file tree is to be included     * @return a new chat request containing the original instructions and the injected     * file tree context     */
    private ChatClientRequest augmentRequestWithFileTree(ChatClientRequest request, Long projectId) {

        List<Message> incomingMessages = request.prompt().getInstructions();

        Message systemMessage = incomingMessages.stream()
                .filter(m -> m.getMessageType() == MessageType.SYSTEM)
                .findFirst()
                .orElse(null);

        List<Message> userMessages = incomingMessages.stream()
                .filter(m -> m.getMessageType() != MessageType.SYSTEM)
                .toList();

        List<Message> allMessages = new ArrayList<>();

        // Add original system message
        if (systemMessage != null) {
            allMessages.add(systemMessage);
        }

        List<FileNode> fileTree = projectFileService.getFileTree(projectId);
        String fileTreeContext = "\n\n ----- FILE TREE ----- \n" + fileTree.toString();
        allMessages.add(new SystemMessage(fileTreeContext));

        allMessages.addAll(userMessages);

        return request.mutate()
                .prompt(new Prompt(allMessages, request.prompt().getOptions()))
                .build();
    }

    @Override
    public String getName() {
        return "FileTreeContextAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
