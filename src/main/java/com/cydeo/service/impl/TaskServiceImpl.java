package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.Task;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.TaskRepository;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final MapperUtil mapperUtil;
    private final TaskRepository taskRepository;
    private final UserService userService;

    public TaskServiceImpl(MapperUtil mapperUtil, TaskRepository taskRepository, UserService userService) {
        this.mapperUtil = mapperUtil;
        this.taskRepository = taskRepository;
        this.userService = userService;
    }


    @Override
    public List<TaskDTO> listAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(entity -> mapperUtil.convertToDto(entity, TaskDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void save(TaskDTO taskDTO) {
        taskDTO.setAssignedDate(LocalDate.now());
        taskDTO.setTaskStatus(Status.OPEN);
        taskRepository.save(mapperUtil.convertToEntity(taskDTO, Task.class));
    }

    @Override
    public void update(TaskDTO taskDTO) {
        Optional<Task> taskToBeUpdated = taskRepository.findById(taskDTO.getId());

        Task convertedTask = mapperUtil.convertToEntity(taskDTO, Task.class);

        if (taskToBeUpdated.isPresent()) {
            convertedTask.setTaskStatus(taskDTO.getTaskStatus() == null ? taskToBeUpdated.get().getTaskStatus() : taskDTO.getTaskStatus());
            convertedTask.setAssignedDate(taskToBeUpdated.get().getAssignedDate());
            taskRepository.save(convertedTask);
        }
    }

    @Override
    public void delete(Long id) {
        Optional<Task> taskToBeDeleted = taskRepository.findById(id);
        if (taskToBeDeleted.isPresent()) {
            taskToBeDeleted.get().setDeleted(true);
            taskRepository.save(taskToBeDeleted.get());
        }
    }

    @Override
    public TaskDTO findById(Long id) {

        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            return mapperUtil.convertToDto(task, TaskDTO.class);
        }
        return null;

//        return mapperUtil.convertToDto(taskRepository.findById(id).get(), TaskDTO.class);
    }

    @Override
    public Integer totalNonCompletedTasks(String projectCode) {
        return taskRepository.totalNonCompletedTasks(projectCode);
    }

    @Override
    public Integer totalCompletedTasks(String projectCode) {
        return taskRepository.totalCompletedTasks(projectCode);
    }

    @Override
    public void deleteByProject(ProjectDTO projectDTO) {
        Project project = mapperUtil.convertToEntity(projectDTO, Project.class);
        List<Task> tasks = taskRepository.findAllByProject(project);
        tasks.forEach(task -> delete(task.getId()));
    }

    @Override
    public void completeByProject(ProjectDTO projectDTO) {
        Project project = mapperUtil.convertToEntity(projectDTO, Project.class);
        List<Task> tasks = taskRepository.findAllByProject(project);

        tasks.stream()
                .map(task->mapperUtil.convertToDto(task, TaskDTO.class))
                .forEach(taskDTO -> {
                    taskDTO.setTaskStatus(Status.COMPLETE);
                    update(taskDTO);
                });

    }

    @Override
    public List<TaskDTO> listAllTasksByStatusIsNot(Status status) {

        UserDTO loggedInUser = userService.findByUserName("sameen@employee.com");
        User user = mapperUtil.convertToEntity(loggedInUser, User.class);

        List<Task> taskList = taskRepository.findAllByTaskStatusIsNotAndAndAssignedEmployee(Status.COMPLETE, user);

        return taskList.stream()
                .map(task-> mapperUtil.convertToDto(task, TaskDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTasksByStatus(Status status) {
        UserDTO loggedInUser = userService.findByUserName("sameen@employee.com");
        User user = mapperUtil.convertToEntity(loggedInUser, User.class);

        List<Task> taskList = taskRepository.findAllByTaskStatusAndAndAssignedEmployee(Status.COMPLETE, user);

        return taskList.stream()
                .map(task-> mapperUtil.convertToDto(task, TaskDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatus(TaskDTO taskDTO) {
        Task taskToBeUpdated = mapperUtil.convertToEntity(taskDTO, Task.class);
        taskToBeUpdated.setTaskStatus(taskDTO.getTaskStatus());
        update(taskDTO);
    }
}
