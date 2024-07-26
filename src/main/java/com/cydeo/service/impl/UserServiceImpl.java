package com.cydeo.service.impl;

import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserDTO> listAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {
        return userMapper.convertToDto(userRepository.findByUserName(username));
    }

    @Override
    public void save(UserDTO user) {

        userRepository.save(userMapper.convertToEntity(user));

    }

    @Override
    public UserDTO update(UserDTO user) {
        //find current user
        User userToBeUpdated = userRepository.findByUserName(user.getUserName()); // has id

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
    public void deleteByUserName(String user) {

    }
}
