package com.gruposuporte.projetosuporte.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="TB_Call")
public class Call {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private Date data;

    @ManyToOne
    @JoinColumn(name = "costumerId")
    private User costumer;

    @ManyToMany
    @JoinColumn(name = "agentId")
    private List<User> agent;


}
