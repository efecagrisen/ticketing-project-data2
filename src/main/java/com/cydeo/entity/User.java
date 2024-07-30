package com.cydeo.entity;

import com.cydeo.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DialectOverride;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
//@Where(clause = "is_deleted = false") //when a user is deleted and a project is tried to be completed which has tasks of the already deleted user, it couldn't get the task as the assigned user is not found. so this where clause is commented to fix it
public class User extends BaseEntity {

    private String firstName;
    private String lastName;
    private String userName;
    private String passWord;
    private boolean enabled;
    private String phone;

    @ManyToOne
    private Role role;

    @Enumerated(EnumType.STRING)
    private Gender gender;

}
