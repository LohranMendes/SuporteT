package com.gruposuporte.projetosuporte.repository;

import com.gruposuporte.projetosuporte.data.Call;
import com.gruposuporte.projetosuporte.data.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<Message, UUID> {

    List<Message> getMessagesByCall(Call call);

}
