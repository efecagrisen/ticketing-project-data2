package com.cydeo.service;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;

import java.util.List;

public interface TaskService {

    List<TaskDTO> listAllTasks();
    void save(TaskDTO taskDTO);
    void update(TaskDTO taskDTO);
    void delete(Long id);
    TaskDTO findById(Long id);

    Integer totalNonCompletedTasks(String projectCode);
    Integer totalCompletedTasks(String projectCode);

    void deleteByProject(ProjectDTO projectDTO);
}
