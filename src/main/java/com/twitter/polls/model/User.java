package com.twitter.polls.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Size(max = 40)
    private String username;

    @NotBlank
    private String email;
    @OneToMany(fetch = FetchType.LAZY)
    private Role roles;

}
