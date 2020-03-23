package com.twitter.polls.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "roles")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Enumerated(value = EnumType.STRING)
    @Column(length = 60)
    private RoleName roleName;

    @ManyToOne
    private User user;

    public Role(){

    }

    public Role(RoleName roleName){
        this.roleName = roleName;
    }

}
