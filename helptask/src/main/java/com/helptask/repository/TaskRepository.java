package com.helptask.repository;

import com.helptask.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends MongoRepository<Task,String> {

    Page<Task> findByUsuarioIdOrderByDataDesc(String userId, Pageable paginas);

    Page<Task> findByTituloIgnoreCaseContainingAndStatusContainingAndPriorityContainingOrderByDataDesc(
            String titulo, String status, String priority, Pageable paginas);

    Page<Task> findByTituloIgnoreCaseContainingAndStatusContainingAndPriorityContainingAndUsuarioIdOrderByDataDesc(
            String titulo, String status, String priority, Pageable paginas, String idUsuario);

    Page<Task> findByTituloIgnoreCaseContainingAndStatusContainingAndPriorityContainingAndAssigneredUserIdOrderByDataDesc(
            String titulo, String status, String priority, Pageable paginas, String assignedUser);

    Page<Task> findByNumber(Integer numero, Pageable paginas);

}
