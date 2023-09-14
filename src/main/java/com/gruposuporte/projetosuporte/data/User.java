package com.gruposuporte.projetosuporte.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    private String password;
    private UserRole role;// determinante de usuario agent ou consumer
    private String email;
    private String username;

    public User(String name, String password, UserRole role, String email, String username) {
        this.name = name;
        this.password = password;
        this.role = role;
        this.email = email;
        this.username = username;
    }
}

