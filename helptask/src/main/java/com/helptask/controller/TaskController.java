package com.helptask.controller;

import com.helptask.api.dto.Summary;
import com.helptask.api.response.Response;
import com.helptask.entity.ProfileEnum;
import com.helptask.entity.Task;
import com.helptask.entity.Usuario;
import com.helptask.security.jwt.JwtTokenUtil;
import com.helptask.service.TaskService;
import com.helptask.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    protected JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @Operation(summary = "endpoint que insere uma task", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<Response<Task>> create(HttpServletRequest request, @RequestBody Task task,
                                                 BindingResult result){
        Response<Task> taskResponse = new Response<>();
        try {
            validateCreateTask(task, result);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(objectError ->
                        taskResponse.getErrors().add(objectError.getDefaultMessage()));
                return ResponseEntity.badRequest().body(taskResponse);
            }
            task.setUsuario(userFromRequest(request));
            Task taskPersist = taskService.createOrUpdate(taskService.buildTaskInsert(task));
            taskResponse.setData(taskPersist);
        }catch (DuplicateKeyException de){
            taskResponse.getErrors().add("Ticket already registred");
            return ResponseEntity.badRequest().body(taskResponse);
        }catch (Exception e){
            taskResponse.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(taskResponse);
        }

        return ResponseEntity.ok(taskResponse);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @Operation(summary = "endpoint que atualiza uma task", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<Response<Task>> update(HttpServletRequest request, @RequestBody Task task,
                                                 BindingResult result){
        Response<Task> taskResponse = new Response<>();
        try {
            validateUpdateTask(task, result);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(objectError ->
                        taskResponse.getErrors().add(objectError.getDefaultMessage()));
                return ResponseEntity.badRequest().body(taskResponse);
            }
            Task taskPersist = taskService.createOrUpdate(taskService.buildTaskUpdate(task));
            taskResponse.setData(taskPersist);
        }catch (Exception e){
            taskResponse.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(taskResponse);
        }

        return ResponseEntity.ok(taskResponse);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    @Operation(summary = "endpoint que busca uma task por id", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<Response<Task>> findById(@PathVariable("id") String id) {
        Response<Task> taskResponse = new Response<>();
        Optional<Task> taskFind = taskService.findById(id);
        if(!taskFind.isPresent()){
            taskResponse.getErrors().add("Register not found id: "+id);
            return ResponseEntity.badRequest().body(taskResponse);
        }
        taskFind.ifPresent(task -> {
            task = taskService.listChangeStatus(task);
            taskResponse.setData(task);
        });
        return ResponseEntity.ok(taskResponse);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @Operation(summary = "endpoint que deleta uma task", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<Response<String>> delete(@PathVariable("id") String id) {
        Response<String> taskResponse = new Response<>();
        Optional<Task> taskFind = taskService.findById(id);
        if(!taskFind.isPresent()){
            taskResponse.getErrors().add("Register not found id: "+id);
            return ResponseEntity.badRequest().body(taskResponse);
        }
        taskService.delete(taskFind.get().getId());
        return ResponseEntity.ok(new Response<String>());
    }

    @GetMapping(value = "/")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    @Operation(summary = "endpoint que mostra todos as tasks", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<Response<Iterable<Task>>> findAll() {
        Response<Iterable<Task>> taksResponse = new Response<>();
        Iterable<Task> tasks = taskService.findAll();
        taksResponse.setData(tasks);
        return ResponseEntity.ok(taksResponse);
    }

    @GetMapping(value = "/{page}/{count}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    @Operation(summary = "endpoint que listas as tasks por página", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<Response<Page<Task>>> listTasks(HttpServletRequest request, @PathVariable("page") int page,
                                                           @PathVariable("count") int count) {
        Response<Page<Task>> tasksResponse = new Response<>();
        Usuario usuario = userFromRequest(request);
        Page<Task> tasks = null;
        if(ProfileEnum.ROLE_TECHNICIAN == usuario.getProfile()){
            tasks = taskService.listTasks(page, count);
        }else if(ProfileEnum.ROLE_CUSTOMER == usuario.getProfile()){
            tasks = taskService.findByCurrentUser(page, count,usuario.getId());
        }
        tasksResponse.setData(tasks);
        return ResponseEntity.ok(tasksResponse);
    }

    @GetMapping(value = "/{page}/{count}/{titulo}/{status}/{priority}/{number}/{dataAgendamento}/{assigned}")
    @Operation(summary = "endpoint que lista task(s) por parâmetros", security = @SecurityRequirement(name = "Authorization"))
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public ResponseEntity<Response<Page<Task>>> findByParams(HttpServletRequest request,
                                                             @PathVariable("page") int page,
                                                             @PathVariable("count") int count,
                                                             @PathVariable("titulo") String titulo,
                                                             @PathVariable("status") String status,
                                                             @PathVariable("priority") String priority,
                                                             @PathVariable("number") Integer number,
                                                             @PathVariable("dataAgendamento") String dataAgendamento,
                                                             @PathVariable("assigned") boolean assigned) throws ParseException {
        Response<Page<Task>> taskResponse = new Response<>();
        Usuario usuario = userFromRequest(request);

        Page<Task> tasks = null;
        titulo = titulo.equals("uninformed") ? "" :titulo.toUpperCase();
        priority = priority.equals("uninformed") ? "" :priority;
        status = status.equals("uninformed") ? "" :status;
        if(number > 0){
            tasks = taskService.findByNumber(page, count, number);
        }else {
             if (ProfileEnum.ROLE_TECHNICIAN.equals(usuario.getProfile())) {
                 if(assigned) {
                     tasks = taskService.findByParametersAndAssignedUser(page, count, titulo, status, priority, usuario.getId());
                 }else{
                     tasks = taskService.findByParameters(page, count, titulo, status, priority);
                 }
            } else if (ProfileEnum.ROLE_CUSTOMER.equals(usuario.getProfile())) {
                tasks = taskService.findByParametersAndCurrentUser(page, count, titulo, status, priority, usuario.getId());
            }
        }
        taskResponse.setData(tasks);
        return ResponseEntity.ok(taskResponse);
    }

    @PutMapping("/{id}/{status}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    @Operation(summary = "endpoint que muda o status da task", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<Response<Task>> changeStatus(HttpServletRequest request,
                                                       @PathVariable("status") String status,
                                                       @PathVariable("id") String id,
                                                       @RequestBody Task task,
                                                       BindingResult result){
        Response<Task> taskResponse = new Response<>();
        Usuario usuario = userFromRequest(request);
        try {
            validateChangeStatus(id , status, result);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(objectError ->
                        taskResponse.getErrors().add(objectError.getDefaultMessage()));
                return ResponseEntity.badRequest().body(taskResponse);
            }
            Optional<Task> taskFind = taskService.findById(task.getId());
            taskResponse.setData(taskService.buildChangeStatus(taskFind, status,usuario));
        }catch (Exception e){
            taskResponse.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(taskResponse);
        }

        return ResponseEntity.ok(taskResponse);
    }
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    @Operation(summary = "endpoint que da um resumo da quantidade de tasks abertas por status", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<Response<Summary>> findSummary(){
        Response<Summary> summaryResponse = new Response<>();

        Iterable<Task> tasks = taskService.findAll();
        summaryResponse.setData(taskService.buildSummary(tasks));

        return ResponseEntity.ok(summaryResponse);
    }

    private void validateChangeStatus(String id, String status, BindingResult result) {
        if(id == null || id.equals("")){
            result.addError(new ObjectError("ChangeStatus", "Id no Information"));
            return;
        }
        if(status == null || status.equals("")){
            result.addError(new ObjectError("ChangeStatus", "status no Information"));
        }
    }

    private void validateCreateTask(Task task, BindingResult result){
        if(task.getTitulo() == null){
            result.addError(new ObjectError("Task", "titulo no Information"));
        }
    }

    private void validateUpdateTask(Task usuario, BindingResult result){
        if(usuario.getId() == null){
            result.addError(new ObjectError("Task", "Id no Information"));
        }
        if(usuario.getTitulo() == null){
            result.addError(new ObjectError("Task", "Titulo no Information"));
        }
    }

    private Usuario userFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String email = jwtTokenUtil.getUserNameFromToken(token);
        return usuarioService.findByEmail(email);
    }

}
