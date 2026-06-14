package com.anee.projects.lovable_clone.service.impl;

import com.anee.projects.lovable_clone.dto.project.FileContentResponse;
import com.anee.projects.lovable_clone.dto.project.FileNode;
import com.anee.projects.lovable_clone.service.ProjectFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProjectFileServiceImpl implements ProjectFileService {

    @Override
    public List<FileNode> getFileTree(Long projectId, Long userId) {
        return List.of();
    }

    @Override
    public FileContentResponse getFileContent(Long projectId, Long userId, String path) {
        return null;
    }

    @Override
    public void saveFile(Long projectId, String filePath, String fileContent) {
        log.info("Saving file: {}", filePath);
        // Save the file Metadata in postgres
        // Save the content in minio
    }
}
