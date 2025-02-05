package com.cydeo.service;

import com.cydeo.dto.UserDTO;

import java.util.List;

public interface UserService {

    List<UserDTO> listAllUsers();
    UserDTO findByUserName(String username);
    void save(UserDTO user);
    UserDTO update(UserDTO user);
    void deleteByUserName(String user);

    List<UserDTO> findByRole(String role);
}
