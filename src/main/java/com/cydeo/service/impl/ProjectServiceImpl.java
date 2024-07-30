package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.mapper.ProjectMapper;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserService userService;
    private final MapperUtil mapperUtil;
    private final TaskService taskService;

    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper, UserService userService, MapperUtil mapperUtil, TaskService taskService) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.userService = userService;
        this.mapperUtil = mapperUtil;
        this.taskService = taskService;
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
    public void deleteByProjectCode(String projectCode) {
        Project projectToBeDeleted = projectRepository.findByProjectCode(projectCode);
        projectToBeDeleted.setDeleted(true);

        projectToBeDeleted.setProjectCode(projectToBeDeleted.getProjectCode()+"-"+projectToBeDeleted.getId());

        projectRepository.save(projectToBeDeleted);

        taskService.deleteByProject(mapperUtil.convertToDto(projectToBeDeleted,ProjectDTO.class));
    }

    @Override
    public void completeByProjectCode(String projectCode) {
        Project project = projectRepository.findByProjectCode(projectCode);
        project.setProjectStatus(Status.COMPLETE);

        projectRepository.save(project);

        taskService.completeByProject(mapperUtil.convertToDto(project,ProjectDTO.class));

    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() {

        UserDTO loggedInUser = userService.findByUserName("harold@manager.com");

        User user = mapperUtil.convertToEntity(loggedInUser, User.class);

        List<Project> projectList = projectRepository.findAllByAssignedManager(user);

        return projectList
                .stream()
                .map(entity-> {
                        ProjectDTO obj = mapperUtil.convertToDto(entity,ProjectDTO.class);

                        obj.setUnfinishedTaskCounts(taskService.totalNonCompletedTasks(entity.getProjectCode()));

                        obj.setCompleteTaskCounts(taskService.totalCompletedTasks(entity.getProjectCode()));

                        return obj;
                    }
                )
                .collect(Collectors.toList());
    }


}
