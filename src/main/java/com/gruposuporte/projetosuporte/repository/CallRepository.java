package com.gruposuporte.projetosuporte.repository;

import com.gruposuporte.projetosuporte.data.Call;
import com.gruposuporte.projetosuporte.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CallRepository extends JpaRepository<Call, UUID> {
    @Modifying
    @Query("update Call c set c.status = false where c.id = :id")
    void closeCall(UUID id);
//    List<Message> getAllBy(UUID id);

    List<Call> findAllByConsumerOrderByDataAsc(User user);

    List<Call> findAllByConsumerOrderByDataDesc(User user);
}
