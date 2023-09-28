package com.gruposuporte.projetosuporte.utils;


import com.gruposuporte.projetosuporte.data.User;
import com.gruposuporte.projetosuporte.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class UserUtils {

    private UserRepository repository;

    @Autowired
    public UserUtils(UserRepository repository) {
        this.repository = repository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return null;
        String username = authentication.getName();
        var user = repository.findByUsername(username);
        return user.orElse(null);
    }


    public String getFormattedMessageDate(Date creationDate) {
        return getFormattedDate(creationDate).replace("Criado", "Enviado");
    }


    public static String getFormattedDate(Date creationDate) {
        Date currentDate = new Date();

        long timeDifferenceInMillis = currentDate.getTime() - creationDate.getTime();

        long days = TimeUnit.MILLISECONDS.toDays(timeDifferenceInMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(timeDifferenceInMillis) - days * 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceInMillis) - TimeUnit.HOURS.toMinutes(hours);

        String message;
        if (days > 0) {
            message = "Criado há " + days + (days == 1 ? " dia atrás" : " dias atrás");
        } else if (hours > 0) {
            message = "Criado há " + hours + (hours == 1 ? " hora atrás" : " horas atrás");
        } else if (minutes > 0) {
            message = "Criado há " + minutes + (minutes == 1 ? " minuto atrás" : " minutos atrás");
        } else {
            message = "Criado agora mesmo";
        }

        return message;
    }

}
