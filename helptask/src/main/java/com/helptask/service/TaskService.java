package com.helptask.service;

import com.helptask.api.dto.Summary;
import com.helptask.entity.ChangeStatus;
import com.helptask.entity.Task;
import com.helptask.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface TaskService {
    Task createOrUpdate(Task task);
    Optional<Task> findById(String id);
    void delete(String id);
    Page<Task> listTasks(int page, int count);
    ChangeStatus createChangesStatus(ChangeStatus changeStatus);
    Task listChangeStatus(Task task);
    Page<Task> findByCurrentUser(int page, int count, String idUsuario);
    Page<Task> findByParameters(int page, int count, String titulo, String status, String priority);
    Page<Task> findByParametersAndCurrentUser(int page, int count, String titulo, String status, String priority, String idUsuario);
    Page<Task> findByNumber(int page, int count, Integer number);
    Iterable<Task> findAll();
    Page<Task> findByParametersAndAssignedUser(int page, int count, String titulo, String status, String priority, String assignedUser);
    Integer generatedNumber();
    Task buildTaskInsert(Task task);
    Task buildTaskUpdate(Task task);
    Task buildChangeStatus(Optional<Task> task, String status, Usuario usuario);
    Summary buildSummary(Iterable<Task> tasks);
}
