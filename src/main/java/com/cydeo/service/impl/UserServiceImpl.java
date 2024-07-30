package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MapperUtil mapperUtil;
    private final ProjectService projectService;
    private final TaskService taskService;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, MapperUtil mapperUtil, @Lazy ProjectService projectService, @Lazy TaskService taskService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.mapperUtil = mapperUtil;
        this.projectService = projectService;
        this.taskService = taskService;
    }

    //@Where is commented in UserDto that's why IsDeleted(false) added in the repository methods.

    @Override
    public List<UserDTO> listAllUsers() {
        return userRepository.findAllByIsDeletedOrderByFirstNameDesc(false)
                .stream()
                .map(userMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {
        return userMapper.convertToDto(userRepository.findByUserNameAndIsDeleted(username,false));
    }

    @Override
    public void save(UserDTO user) {

        userRepository.save(userMapper.convertToEntity(user));

    }

    @Override
    public UserDTO update(UserDTO user) {
        //find current user
        User userToBeUpdated = userRepository.findByUserNameAndIsDeleted(user.getUserName(),false); // has id

        //Map update user dto to entity object
        User convertedUser = userMapper.convertToEntity(user); // no id yet

        //set Id to the converted object
        convertedUser.setId(userToBeUpdated.getId());

        //save the updated user in DB
        userRepository.save(convertedUser);

        //return the object
        return findByUserName(user.getUserName());
    }

    @Override
    public void deleteByUserName(String username) {

        User userToBeDeleted = userRepository.findByUserNameAndIsDeleted(username, false);

        if (checkIfUserBeDeleted(mapperUtil.convertToEntity(findByUserName(username), User.class))){

            userToBeDeleted.setDeleted(true);
            userToBeDeleted.setUserName(userToBeDeleted.getUserName()+"-"+userToBeDeleted.getId());
            userRepository.save(userToBeDeleted);
        }
    }

    @Override
    public List<UserDTO> findByRole(String role) {

        List<User> userListByRole = userRepository.findByRoleDescriptionIgnoreCaseAndIsDeleted(role,false);

        return userListByRole.stream()
                .map(userMapper::convertToDto)
                .collect(Collectors.toList());

    }

    private boolean checkIfUserBeDeleted(User user){ //as it is private we can pass entity obj

        switch (user.getRole().getDescription()){
            case "Manager":
                List<ProjectDTO> projectDTOList = projectService.listAllNonCompletedByAssignedManager(mapperUtil.convertToDto(user,UserDTO.class));
                return  projectDTOList.size() == 0;
            case "Employee":
                List<TaskDTO> taskDTOList = taskService.listAllNonCompletedByAssignedEmployee(mapperUtil.convertToDto(user,UserDTO.class));
                return  taskDTOList.size() == 0;
            default:
                return true;
        }

    }




}
