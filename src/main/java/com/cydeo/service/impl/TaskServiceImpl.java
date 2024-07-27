package com.cydeo.service.impl;

import com.cydeo.dto.TaskDTO;
import com.cydeo.entity.Task;
import com.cydeo.enums.Status;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.TaskRepository;
import com.cydeo.service.TaskService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final MapperUtil mapperUtil;
    private final TaskRepository taskRepository;

    public TaskServiceImpl(MapperUtil mapperUtil, TaskRepository taskRepository) {
        this.mapperUtil = mapperUtil;
        this.taskRepository = taskRepository;
    }


    @Override
    public List<TaskDTO> listAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(entity-> mapperUtil.convertToDto(entity,TaskDTO.class))
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

        if (taskToBeUpdated.isPresent()){
            convertedTask.setTaskStatus(taskToBeUpdated.get().getTaskStatus());
            convertedTask.setAssignedDate(taskToBeUpdated.get().getAssignedDate());
            taskRepository.save(convertedTask);
        }
    }

    @Override
    public void delete(Long id) {
        Optional<Task> taskToBeDeleted = taskRepository.findById(id);
        if (taskToBeDeleted.isPresent()){
            taskToBeDeleted.get().setDeleted(true);
            taskRepository.save(taskToBeDeleted.get());
            }
        }

    @Override
    public TaskDTO findById(Long id) {

        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()){
            return mapperUtil.convertToDto(task,TaskDTO.class);
        }
        return null;

//        return mapperUtil.convertToDto(taskRepository.findById(id).get(), TaskDTO.class);
    }
}
