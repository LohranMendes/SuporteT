package com.gruposuporte.projetosuporte.data;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
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

    private String image;

    @Column(length = 300)
    private String title;

    private boolean status;

    @Column(length = 780)
    private String description;

    @ManyToOne
    @JoinColumn(name = "costumerId")
    private User consumer;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="agenteId")
    private User agent;

    public Call(Date data, String title, boolean status, String description, User consumer) {
        this.data = data;
        this.title = title;
        this.status = status;
        this.description = description;
        this.consumer = consumer;
//        this.agents = new ArrayList<>();
    }
    @Transient
    public String getImagePath(){
        if(image==null || id==null){
            return null;
        }
        return "/uploads/"+consumer.getId()+"/"+image;
    }
}
