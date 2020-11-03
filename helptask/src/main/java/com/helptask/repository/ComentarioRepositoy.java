package com.helptask.repository;

import com.helptask.entity.Comentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentarioRepositoy extends MongoRepository<Comentario, String> {

    Page<Comentario> findByTask(Pageable pageable,String taskId);

}
