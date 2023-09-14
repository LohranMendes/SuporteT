package com.gruposuporte.projetosuporte.repository;

import com.gruposuporte.projetosuporte.data.Call;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CallRepository extends JpaRepository<Call, UUID> {

//    List<Message> getAllBy(UUID id);

}
