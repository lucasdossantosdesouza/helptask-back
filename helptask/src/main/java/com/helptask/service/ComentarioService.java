package com.helptask.service;

import com.helptask.entity.Comentario;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface ComentarioService {
    Comentario createOrUpdate(Comentario comentario);
    Optional<Comentario> findById(String id);
    void delete(String id);
    Page<Comentario> findByTask(int page, int count,String idTask);
    Comentario buildInsereComentario(Comentario comentario);
    Comentario buildComentarioUpdate(Optional<Comentario> comentarioFind, Comentario comentario);
}
