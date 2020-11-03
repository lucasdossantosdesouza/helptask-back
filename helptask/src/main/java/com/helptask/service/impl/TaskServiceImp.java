package com.helptask.service.impl;

import com.helptask.api.dto.Summary;
import com.helptask.entity.ChangeStatus;
import com.helptask.entity.StatusEnum;
import com.helptask.entity.Task;
import com.helptask.entity.Usuario;
import com.helptask.repository.ChangeStatusRepository;
import com.helptask.repository.ComentarioRepositoy;
import com.helptask.repository.TaskRepository;
import com.helptask.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class TaskServiceImp implements TaskService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ChangeStatusRepository changeStatusRepository;

    @Autowired
    ComentarioRepositoy comentarioRepositoy;

    @Override
    public Task createOrUpdate(Task task) {
        return taskRepository.save(task);
    }

    public Integer generatedNumber() {
        Random random = new Random();
        return random.nextInt(9999);
    }

    @Override
    public Task buildTaskInsert(Task task) {
        task.setData(new Date());
        task.setNumber(generatedNumber());
        task.setTitulo(task.getTitulo().toUpperCase());
        return task;
    }
    public Task buildTaskUpdate(Task task){
        Optional<Task> taskFind = findById(task.getId());
        taskFind.ifPresent(task1 -> {
                    task.setStatus(task1.getStatus());
                    task.setData(task1.getData());
                    task.setNumber(task1.getNumber());
                    task.setUsuario(task1.getUsuario());
                    task.setTitulo(task.getTitulo().toUpperCase());
                    task.setDescription(task.getDescription());
                    task.setDataAgendamento(task.getDataAgendamento());
                    if (task1.getAssigneredUser() != null) {
                        task.setAssigneredUser(task1.getAssigneredUser());
                    }
         }
       );
        return task;
    }

    @Override
    public Task buildChangeStatus(Optional<Task> taskFind, String status, Usuario usuario) {
        AtomicReference<Task> taskPersist = new AtomicReference<>();
        taskFind.ifPresent(task1 -> {
            task1.setStatus(StatusEnum.getStatus(status));
            if(status.equals("Assigned")){
                task1.setAssigneredUser(new Usuario());
                task1.setAssigneredUser(usuario);
            }
            taskPersist.set(createOrUpdate(task1));
            ChangeStatus changeStatus = new ChangeStatus();
            changeStatus.setUsuario(usuario);
            changeStatus.setData(new Date());
            changeStatus.setStatusEnum(StatusEnum.getStatus(status));
            changeStatus.setTask(taskPersist.get());
            createChangesStatus(changeStatus);

        });
        return taskPersist.get();
    }

    @Override
    public Summary buildSummary(Iterable<Task> tasks) {
        AtomicReference<Integer> amountNew = new AtomicReference<>(0);
        AtomicReference<Integer> amountAssigned = new AtomicReference<>(0);
        AtomicReference<Integer> amountResolved = new AtomicReference<>(0);
        AtomicReference<Integer> amountAproved = new AtomicReference<>(0);
        AtomicReference<Integer> amountDisaproved = new AtomicReference<>(0);
        AtomicReference<Integer> amountClosed = new AtomicReference<>(0);
        Summary summary = new Summary();

        tasks.forEach(task -> {
            if(StatusEnum.New.equals(task.getStatus())){
                amountNew.getAndSet(amountNew.get() + 1);
            }
            if(StatusEnum.Resolved.equals(task.getStatus())){
                amountResolved.getAndSet(amountResolved.get() + 1);
            }
            if(StatusEnum.Aproved.equals(task.getStatus())){
                amountAproved.getAndSet(amountAproved.get() + 1);
            }
            if(StatusEnum.Disaproved.equals(task.getStatus())){
                amountDisaproved.getAndSet(amountDisaproved.get() + 1);
            }
            if(StatusEnum.Assigned.equals(task.getStatus())){
                amountAssigned.getAndSet(amountAssigned.get() + 1);
            }
            if(StatusEnum.Closed.equals(task.getStatus())){
                amountClosed.getAndSet(amountClosed.get() + 1);
            }
        });
        summary.setAmountNew(amountNew.get());
        summary.setAmountResolved(amountResolved.get());
        summary.setAmountAproved(amountAproved.get());
        summary.setAmountDisaproved(amountDisaproved.get());
        summary.setAmountAssigned(amountAssigned.get());
        summary.setAmountClosed(amountClosed.get());

        return summary;
    }

    @Override
    public Optional<Task> findById(String id) {
        return taskRepository.findById(id);
    }

    @Override
    public void delete(String id) {
        taskRepository.deleteById(id);
    }

    @Override
    public Page<Task> listTasks(int page, int count) {
        Pageable pageable = PageRequest.of(page,count);
        return taskRepository.findAll(pageable);
    }

    @Override
    public ChangeStatus createChangesStatus(ChangeStatus changeStatus) {
        return changeStatusRepository.save(changeStatus);
    }

    @Override
    public Task listChangeStatus(Task taskFind) {
       Iterable<ChangeStatus> changeStatuses = changeStatusRepository.
               findByTaskIdOrderByDataDesc(taskFind.getId());
        taskFind.setChangeStatus(new ArrayList<>());
        changeStatuses.forEach(changeStatus -> {
            changeStatus.setTask(null);
            taskFind.getChangeStatus().add(changeStatus);
        });
        return taskFind;
    }

    @Override
    public Page<Task> findByCurrentUser(int page, int count, String idUsuario) {
        Pageable pageable= PageRequest.of(page,count);
        return taskRepository.findByUsuarioIdOrderByDataDesc(idUsuario, pageable);
    }

    @Override
    public Page<Task> findByParameters(int page, int count, String titulo, String status, String priority) {
        Pageable pageable = PageRequest.of(page,count);
        return taskRepository.findByTituloIgnoreCaseContainingAndStatusContainingAndPriorityContainingOrderByDataDesc(titulo, status, priority, pageable);
    }

    @Override
    public Page<Task> findByParametersAndCurrentUser(int page, int count, String titulo, String status, String priority, String idUsuario) {
        Pageable pageable = PageRequest.of(page,count);
        return taskRepository.findByTituloIgnoreCaseContainingAndStatusContainingAndPriorityContainingAndUsuarioIdOrderByDataDesc(titulo, status, priority, pageable, idUsuario);
    }

    @Override
    public Page<Task> findByNumber(int page, int count, Integer number) {
        Pageable pageable = PageRequest.of(page,count);
        return taskRepository.findByNumber(number,pageable);
    }

    @Override
    public Iterable<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public Page<Task> findByParametersAndAssignedUser(int page, int count, String titulo, String status, String priority, String assignedUser) {
        Pageable pageable = PageRequest.of(page,count);
        return taskRepository.findByTituloIgnoreCaseContainingAndStatusContainingAndPriorityContainingAndAssigneredUserIdOrderByDataDesc(titulo, status, priority, pageable,assignedUser);
    }
}
