package com.anee.projects.lovable_clone.llm.tools;

import com.anee.projects.lovable_clone.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.ArrayList;
import java.util.List;

/**
 * <h3>CodeGenerationTools</h3>
 *
 * <p>Utility class exposing file-reading capabilities as a tool for the LLM-based code generation
 * workflow. Instances are created with a {@code ProjectFileService} and a {@code projectId}
 * (injected via Lombok's {@code @RequiredArgsConstructor}). The class is intentionally small and
 * focused: it normalizes requested file paths, reads file contents from the project, wraps each
 * file's content with clear delimiters, and returns the results as a list of strings for
 * downstream LLM consumption.
 *
 * Responsibilities
 * - Accept a list of relative file paths (e.g., {@code src/App.tsx}) and remove any leading '/'.
 * - Log each file read with the associated {@code projectId} and normalized path.
 * - Retrieve file content using {@code projectFileService.getFileContent(projectId, path).content()}.
 * - Return a {@code List<String>} where each element contains:
 *     --- START OF FILE: <relative-path> ---
 *     <file content>
 *     --- END OF FILE:  ---
 *
 * Important details
 * - Input contract: callers must supply relative paths that exist inside the project's FILE_TREE.
 *   The tool explicitly warns not to pass paths outside the FILE_TREE.
 * - Error handling: the method does not catch exceptions from {@code ProjectFileService}; such
 *   exceptions will propagate to the caller.
 * - Thread-safety: the class holds only final references and is stateless beyond those dependencies.
 *   It is safe to use concurrently provided {@code ProjectFileService} is thread-safe.
 *
 * Usage example
 * <pre>
 *   CodeGenerationTools tools = new CodeGenerationTools(projectFileService, projectId);
 *   List<String> files = tools.readFiles(Arrays.asList("src/App.tsx", "pom.xml"));
 * </pre>
 *
 * Notes
 * - The returned string format (delimiters and path header) is designed for easy parsing by LLM
 *   prompts and consumers; changing it requires updating any consumers that parse these outputs.
 */
@RequiredArgsConstructor
@Slf4j
public class CodeGenerationTools {

    private final ProjectFileService projectFileService;
    private final Long projectId;

    @Tool(name = "read_files",
            description = "Read the content of files. Only input the file names present inside the FILE_TREE. DO NOT input any path which is not present under the FILE_TREE.")
    public List<String> readFiles(
            @ToolParam(description = "List of relative paths (e.g., ['src/App.tsx'])")
            List<String> paths) {

        List<String> result = new ArrayList<>();

        for (String path : paths) {
            String cleanPath = path.startsWith("/") ? path.substring(1) : path;

            log.info("Reading file content for projectId: {}, path: {}", projectId, cleanPath);

            String content = projectFileService.getFileContent(projectId, cleanPath).content();

            result.add(String.format(
                    "--- START OF FILE: %s ---\n%s\n--- END OF FILE:  ---",
                    cleanPath, content
            ));
        }

        return result;
    }
}
