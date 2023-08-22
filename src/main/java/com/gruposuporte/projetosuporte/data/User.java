package com.gruposuporte.projetosuporte.data;
import jakarta.persistence.*;
import java.util.UUID;
@Entity
@Table(name = "TB_USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    private String password;
    private String role;

}
