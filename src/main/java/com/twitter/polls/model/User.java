package com.twitter.polls.model;

import com.twitter.polls.model.audit.DateAudit;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;


@Data
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "username"
        }),
        @UniqueConstraint(columnNames = {
                "email"
        })
})
@Entity
public class User extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "name")
    @Size(max = 40)
    private String name;

    @NotBlank
    @Size(max = 15)
    @Column(name = "username")
    @NaturalId
    private String username;

    @NotBlank
    @Size(max = 40)
    @Email
    @NaturalId
    @Column(name = "email")
    private String email;

    @NotBlank
    @Size(max = 100)
    @Column(name = "password")
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)

//    private Set<Role> roles = new HashSet<>();
    private Set<Role> role = new HashSet<>();
    public User(){

    }

    public User(String name, String username, String email, String password){
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
