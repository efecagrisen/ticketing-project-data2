package com.cydeo.service;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Role;

import java.util.List;

public interface ProjectService {

    ProjectDTO getByProjectCode(String code);
    List<ProjectDTO> listAllProjects();
    void save(ProjectDTO projectDTO);
    void update( ProjectDTO projectDTO);
    void delete(String code);

    void deleteByProjectCode(String projectCode);

    void completeByProjectCode(String projectCode);

    List<ProjectDTO> listAllProjectDetails();

}
