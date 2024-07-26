package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.entity.Project;
import com.cydeo.enums.Status;
import com.cydeo.mapper.ProjectMapper;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    @Override
    public ProjectDTO getByProjectCode(String code) {
        return projectMapper.convertToDto(projectRepository.findByProjectCode(code));
    }


    @Override
    public List<ProjectDTO> listAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void save(ProjectDTO projectDTO) {
        projectDTO.setProjectStatus(Status.OPEN);
        Project project = projectMapper.convertToEntity(projectDTO);
        projectRepository.save(project);
    }

    @Override
    public void update(ProjectDTO projectDTO) {
        Project projectToBeUpdated = projectRepository.findByProjectCode(projectDTO.getProjectCode());
        Project convertedProject = projectMapper.convertToEntity(projectDTO);
        convertedProject.setId(projectToBeUpdated.getId());
        convertedProject.setProjectStatus(projectToBeUpdated.getProjectStatus());
        projectRepository.save(convertedProject);
    }

    @Override
    public void delete(String code) {

    }

    @Override
    public void deleteByProjectCode(String projectCode) {
        Project projectToBeDeleted = projectRepository.findByProjectCode(projectCode);
        projectToBeDeleted.setDeleted(true);
        projectRepository.save(projectToBeDeleted);
    }

    @Override
    public void completeByProjectCode(String projectCode) {
        Project project = projectRepository.findByProjectCode(projectCode);
        project.setProjectStatus(Status.COMPLETE);
        projectRepository.save(project);
    }



}
