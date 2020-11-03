package com.helptask.repository;

import com.helptask.entity.ChangeStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeStatusRepository extends MongoRepository<ChangeStatus, String> {

    Iterable<ChangeStatus> findByTaskIdOrderByDataDesc(String taskId);
}
