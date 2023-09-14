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

    @Column(length = 300)
    private String title;

    private boolean status;

    @Column(length = 780)
    private String description;

    @ManyToOne
    @JoinColumn(name = "costumerId")
    private User costumer;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "call_agent",
            joinColumns = @JoinColumn(name = "call_id"),
            inverseJoinColumns = @JoinColumn(name = "agent_id")
    )
    private List<User> agents;

    public Call(Date data, String title, boolean status, String description, User costumer) {
        this.data = data;
        this.title = title;
        this.status = status;
        this.description = description;
        this.costumer = costumer;
//        this.agents = new ArrayList<>();
    }
}
