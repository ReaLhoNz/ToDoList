package com.rybka.todolist.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name ="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    private Timestamp timestamp;
    private boolean accountVerified;


    @OneToMany(mappedBy = "user")
    private Set<SecureToken> tokens;

    public User(String username, String email, String password, Timestamp timestamp, boolean accountVerified) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.timestamp = timestamp;
        this.accountVerified = accountVerified;
    }
}
