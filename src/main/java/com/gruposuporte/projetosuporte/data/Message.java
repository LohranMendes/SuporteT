package com.gruposuporte.projetosuporte.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="TB_Message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String text;
    private Date datetime;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    public Message(String text, Date datetime) {
        this.text = text;
        this.datetime = datetime;
    }
}
