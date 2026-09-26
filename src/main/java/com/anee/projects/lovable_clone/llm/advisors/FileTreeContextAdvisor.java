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


/**
 * <h2>FileTreeContextAdvisor</h2>
 *
 * <p>Acts as a {@code StreamAdvisor} that enriches incoming {@link ChatClientRequest}
 * prompts with the repository file tree for a project so downstream language models
 * receive workspace context before generating responses.</p>
 *
 * <h3>Behavior</h3>
 * <ul>
 *   <li>Reads a numeric {@code projectId} from {@code request.context()} (defaults to 0).</li>
 *   <li>Extracts any existing system message from the original prompt and preserves it.</li>
 *   <li>Collects all non-system instructions (user/assistant messages) and defers them.</li>
 *   <li>Retrieves the project's file tree from {@link com.anee.projects.lovable_clone.service.ProjectFileService}.</li>
 *   <li>Creates a new system-level message containing a textual representation of the file tree
 *       and injects it immediately after the preserved system message (if present).</li>
 *   <li>Rebuilds the prompt (preserving prompt options) and returns a mutated request that
 *       is forwarded to the next advisor in the chain.</li>
 * </ul>
 *
 * <h3>Method summaries</h3>
 * <ul>
 *   <li>{@link #adviseStream(ChatClientRequest, StreamAdvisorChain)}:
 *       Entry point called by the advising pipeline. Augments the request and delegates to
 *       {@code streamAdvisorChain.nextStream(...)}.</li>
 *   <li>{@link #augmentRequestWithFileTree(ChatClientRequest, Long)}:
 *       Performs the prompt reconstruction and file-tree injection.</li>
 *   <li>{@link #getName()} / {@link #getOrder()}:
 *       Return advisor identity and ordering metadata used by the advisor chain.</li>
 * </ul>
 *
 * <h3>Thread-safety and side effects</h3>
 * <p>The advisor is effectively stateless: it holds a final reference to
 * {@link com.anee.projects.lovable_clone.service.ProjectFileService} but does not mutate
 * internal state. It is safe for concurrent use provided {@code ProjectFileService} itself
 * is thread-safe.</p>
 *
 * <h3>Error handling</h3>
 * <p>Any exceptions thrown while retrieving the file tree or mutating the request are not
 * caught here and will propagate to the caller. If {@code projectId} is missing or invalid,
 * a default of {@code 0} is used and behavior depends on
 * {@code ProjectFileService#getFileTree} semantics (e.g., returning an empty list).</p>
 *
 * <h3>Resulting prompt structure</h3>
 * <pre>
 * [ optional original SYSTEM message ]
 * [ injected SYSTEM message: "\n\n ----- FILE TREE ----- \n" + fileTree.toString() ]
 * [ original non-system messages (user/assistant) ]
 * </pre>
 *
 * @see com.anee.projects.lovable_clone.service.ProjectFileService
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileTreeContextAdvisor implements StreamAdvisor {

    private final ProjectFileService projectFileService;

    /**
 * Augments an incoming {@link ChatClientRequest} with repository file-tree context and
 * forwards the mutated request to the next advisor in the chain.
 *
 * <p>Reads a numeric {@code projectId} from {@code request.context()} (defaults to {@code 0}),
 * delegates to {@link #augmentRequestWithFileTree(ChatClientRequest, Long)} to rebuild the prompt
 * (injecting a system-level file-tree message), then calls {@code streamAdvisorChain.nextStream(...)}.</p>
 *
 * @param request the incoming chat request whose prompt will be enriched with file-tree context
 * @param streamAdvisorChain the advisor chain used to forward the augmented request
 * @return a {@code Flux<ChatClientResponse>} produced by the downstream advisor
 * @throws NumberFormatException if the {@code projectId} context value is not parseable as a long
 * @throws RuntimeException if file-tree retrieval or request mutation fails (propagated to caller)
 */
    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain streamAdvisorChain) {
        Map<String, Object> context = request.context();
        Long projectId = Long.parseLong(context.getOrDefault("projectId", 0).toString());

        ChatClientRequest augmentedChatClientRequest = augmentRequestWithFileTree(request, projectId);

        return streamAdvisorChain.nextStream(augmentedChatClientRequest);
    }

 /**
 * Injects the project's file tree into the request prompt as a new system message.
 *
  * <p>
 * What it does (step‑by‑step, simply):
  * <ol>
 *   <li>Reads the current prompt messages.</li>
 *   <li>Keeps the first system message found (if any).</li>
 *   <li>Collects all non-system messages to append later.</li>
 *   <li>Fetches the project's file tree using the provided projectId.</li>
 *   <li>Creates a new system message containing a textual representation of that file tree.</li>
 * <li>Rebuilds the prompt so it contains: [original system?] -> [file-tree system] -> [other messages].</li>
 * <li>Returns a mutated ChatClientRequest with the new Prompt (original prompt options are preserved).</li>
  * </ol>
  * </p>
 *
 * Important notes:
 * - Exceptions from parsing projectId or fetching the file tree are not handled here and will propagate.
 * - This method preserves the order of non-system messages.
 */
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
