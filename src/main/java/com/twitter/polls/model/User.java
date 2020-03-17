package com.twitter.polls.model;

import lombok.Data;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Size(max = 15)
    private String username;

    @NotBlank
    @Size(max = 40)
    @Email
    @NaturalId
    private String email;

    @NotBlank
    @Size(max = 100)
    private String password;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();


}
