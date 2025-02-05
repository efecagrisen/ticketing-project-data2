package com.cydeo.repository;

import com.cydeo.entity.Role;
import com.cydeo.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    List<User> findAllByIsDeletedOrderByFirstNameDesc(Boolean deleted);

    //get user based on username
    User findByUserNameAndIsDeleted(String username, Boolean deleted);

    @Transactional
    void deleteByUserName(String username);

    List<User> findByRoleDescriptionIgnoreCaseAndIsDeleted(String roleDescription, Boolean deleted);

}
