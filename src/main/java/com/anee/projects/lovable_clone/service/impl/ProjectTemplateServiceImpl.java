package com.anee.projects.lovable_clone.service.impl;

import com.anee.projects.lovable_clone.entities.Project;
import com.anee.projects.lovable_clone.entities.ProjectFile;
import com.anee.projects.lovable_clone.error.ResourceNotFoundException;
import com.anee.projects.lovable_clone.repository.ProjectFileRepository;
import com.anee.projects.lovable_clone.repository.ProjectRepository;
import com.anee.projects.lovable_clone.service.ProjectTemplateService;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation that initializes a project from a stored template in MinIO.
 * <p>
 * Behavior summary:
 * <ol>
 *   <li>Look up the {@code Project} by id. If not found, throw {@code ResourceNotFoundException}.</li>
 *   <li>List objects in the template bucket (\`starter-projects\`) under the configured template prefix
 *       (\`react-vite-tailwind-daisyui-starter/\`) recursively.</li>
 *   <li>For each template object:
 *       <ul>
 *         <li>Compute a relative path by removing the template prefix.</li>
 *         <li>Copy the object to the target bucket (\`lovable\`) at key {@code "<projectId>/<relative-path>" }.</li>
 *         <li>Create a {@code ProjectFile} metadata record and collect it for persistence.</li>
 *       </ul>
 *   </li>
 *   <li>Persist all collected {@code ProjectFile} records via {@code ProjectFileRepository}.</li>
 * </ol>
 * <p>
 * Important details:
 * <ul>
 *   <li>External I/O (MinIO) is performed per-file; failures may leave partial state in the target bucket.</li>
 *   <li>Exceptions from listing/copying are propagated as {@code RuntimeException} with a generic message.</li>
 *   <li>This implementation assumes the template bucket and target bucket names are valid and accessible
 *       by the configured {@code MinioClient}.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectTemplateServiceImpl implements ProjectTemplateService {

    private final MinioClient minioClient;
    private final ProjectFileRepository projectFileRepository;
    private final ProjectRepository projectRepository;

    private static final String TEMPLATE_BUCKET = "starter-projects";
    private static final String TARGET_BUCKET = "lovable";
    private static final String TEMPLATE_NAME = "react-vite-tailwind-daisyui-starter";

    /**
 * Initialize a project from a stored MinIO template.
 *
 * <p>This method performs the following steps:
 * <ol>
 *   <li>Look up the {@code Project} by {@code projectId}. If not found, a {@code ResourceNotFoundException} is thrown.</li>
 *   <li>List all objects in the MinIO bucket defined by {@code TEMPLATE_BUCKET} under the prefix
 *       {@code TEMPLATE_NAME + "/"} recursively using {@code minioClient.listObjects(...)}.</li>
 *   <li>For each listed object:
 *     <ul>
 *       <li>Compute a relative path {@code cleanPath} by removing the template prefix.</li>
 *       <li>Compose the destination object key as {@code projectId + "/" + cleanPath}.</li>
 *       <li>Copy the object from the template bucket to the {@code TARGET_BUCKET} at the destination key
 *           with {@code minioClient.copyObject(...)}.</li>
 *       <li>Create a {@code ProjectFile} metadata record (linking to the {@code Project}, storing the relative
 *           path, MinIO object key and timestamps) and collect it for persistence.</li>
 *     </ul>
 *   </li>
 *   <li>Persist all collected {@code ProjectFile} records in a single batch via
 *       {@code projectFileRepository.saveAll(...)}.</li>
 * </ol>
 *
 * <p>Important implementation details and behavior:
 * <ul>
 *   <li>All MinIO operations are performed per-file; a failure during processing may leave partially copied
 *       objects in the target bucket. There is no rollback of copied objects in this method.</li>
 *   <li>Any exception thrown while listing or copying objects (or while building metadata) is caught and
 *       rethrown as a {@code RuntimeException} with the original exception as the cause.</li>
 *   <li>Timestamps for {@code createdAt} and {@code updatedAt} are set to {@code Instant.now()} at the time
 *       each metadata record is created.</li>
 *   <li>This implementation assumes the configured {@code MinioClient} has access to the template and target buckets.</li>
 * </ul>
 *
 * @param projectId the id of the project to initialize from the template
 * @throws ResourceNotFoundException if no project exists with the provided {@code projectId}
 * @throws RuntimeException if listing or copying objects fails (original exception is wrapped)
 */

    @Override
    public void initializeProjectFromTemplate(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: ", projectId.toString()));

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(TEMPLATE_BUCKET)
                            .prefix(TEMPLATE_NAME + "/")
                            .recursive(true)
                            .build()
            );

            List<ProjectFile> filesToSave = new ArrayList<>();  // for metadata in postgres db

            for (Result<Item> result : results) {
                Item item = result.get();
                String sourceKey = item.objectName();

                String cleanPath = sourceKey.replaceFirst(TEMPLATE_NAME + "/", "");
                String destKey = projectId + "/" + cleanPath;

                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(TARGET_BUCKET)
                                .object(destKey)
                                .source(
                                        CopySource.builder()
                                                .bucket(TEMPLATE_BUCKET)
                                                .object(sourceKey)
                                                .build()
                                )
                                .build()
                );

                ProjectFile pf = ProjectFile.builder()
                        .project(project)
                        .path(cleanPath)
                        .minioObjectKey(destKey)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                filesToSave.add(pf);
            }

            projectFileRepository.saveAll(filesToSave);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize project from template", e);
        }
    }
}
