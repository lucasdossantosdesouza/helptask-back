package com.helptask.controller;

import com.helptask.api.response.Response;
import com.helptask.entity.Comentario;
import com.helptask.entity.Usuario;
import com.helptask.security.jwt.JwtTokenUtil;
import com.helptask.service.ComentarioService;
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
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/comentario")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @Autowired
    protected JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    @Operation(summary = "endpoint que insere um comentário", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<Response<Comentario>> create(HttpServletRequest request, @RequestBody Comentario comentario,
                                                 BindingResult result){
        Response<Comentario> comentarioResponse = new Response<>();
        try {
            validateComentario(comentario, result);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(objectError ->
                        comentarioResponse.getErrors().add(objectError.getDefaultMessage()));
                return ResponseEntity.badRequest().body(comentarioResponse);
            }
            comentario.setUsuario(userFromRequest(request));
            Comentario comentarioPersist = comentarioService.createOrUpdate(comentarioService.buildInsereComentario(comentario));
            comentarioResponse.setData(comentarioPersist);
        }catch (DuplicateKeyException de){
            comentarioResponse.getErrors().add("Comentario already registred");
            return ResponseEntity.badRequest().body(comentarioResponse);
        }catch (Exception e){
            comentarioResponse.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(comentarioResponse);
        }

        return ResponseEntity.ok(comentarioResponse);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    @Operation(summary = "endpoint que atualiza um comentario", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<Response<Comentario>> update(HttpServletRequest request, @RequestBody Comentario comentario,
                                                 BindingResult result){
        Response<Comentario> comentarioResponse = new Response<>();
        try {
            validateComentario(comentario, result);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(objectError ->
                        comentarioResponse.getErrors().add(objectError.getDefaultMessage()));
                return ResponseEntity.badRequest().body(comentarioResponse);
            }
            Optional<Comentario> comentarioFind = comentarioService.findById(comentario.getId());

            comentarioResponse.setData(comentarioService.buildComentarioUpdate(comentarioFind,comentario));
        }catch (Exception e){
            comentarioResponse.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(comentarioResponse);
        }

        return ResponseEntity.ok(comentarioResponse);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    @Operation(summary = "endpoint que deleta um comentário", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<Response<String>> delete(@PathVariable("id") String id) {
        Response<String> comentarioResponse = new Response<>();
        Optional<Comentario> comentarioFind = comentarioService.findById(id);
        if(!comentarioFind.isPresent()){
            comentarioResponse.getErrors().add("Register not found id: "+id);
            return ResponseEntity.badRequest().body(comentarioResponse);
        }
        comentarioService.delete(comentarioFind.get().getId());
        return ResponseEntity.ok(new Response<String>());
    }

    @GetMapping(value = "/{page}/{count}/{idTask}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    @Operation(summary = "endpoint que busca comentarios pelo id da task", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<Response<Page<Comentario>>> findByTaskId(@PathVariable("idTask") String idTask
                                    ,@PathVariable("page") int page,@PathVariable("count") int count) {
        Response<Page<Comentario>> comentarioResponse = new Response<>();
        Page<Comentario> comentarios = comentarioService.findByTask(page, count,idTask);
        comentarioResponse.setData(comentarios);
        return ResponseEntity.ok(comentarioResponse);
    }

    private void validateComentario(Comentario comentario, BindingResult result){
        if(comentario.getTexto()== null){
            result.addError(new ObjectError("Comentario", "texto no Information"));
        }
    }

    private Usuario userFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String email = jwtTokenUtil.getUserNameFromToken(token);
        return usuarioService.findByEmail(email);
    }

}
