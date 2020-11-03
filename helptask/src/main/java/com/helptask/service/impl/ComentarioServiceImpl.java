package com.helptask.service.impl;

import com.helptask.entity.Comentario;
import com.helptask.entity.Task;
import com.helptask.repository.ComentarioRepositoy;
import com.helptask.repository.TaskRepository;
import com.helptask.service.ComentarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ComentarioServiceImpl implements ComentarioService {

    @Autowired
    private ComentarioRepositoy comentarioRepositoy;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public Comentario createOrUpdate(Comentario comentario) {
        return comentarioRepositoy.save(comentario);
    }

    @Override
    public Optional<Comentario> findById(String id) {
        return comentarioRepositoy.findById(id);
    }

    @Override
    public void delete(String id) {
        comentarioRepositoy.deleteById(id);
    }

    @Override
    public Page<Comentario> findByTask(int page, int count, String idTask) {
        Pageable pageable= PageRequest.of(page,count);
         return comentarioRepositoy.findByTask(pageable, idTask);
    }

    @Override
    public Comentario buildInsereComentario(Comentario comentario) {
        comentario.setData(new Date());
        Optional<Task> task = taskRepository.findById(comentario.getTask().getId());
        task.ifPresent(task1 -> {
            comentario.setTask(task1);
        });
        return comentario;
    }

    @Override
    public Comentario buildComentarioUpdate(Optional<Comentario> comentarioFind, Comentario comentario) {
        AtomicReference<Comentario> comentarioPersist = new AtomicReference<>();
        comentarioFind.ifPresent(comentario1 -> {
            comentario.setUsuario(comentario1.getUsuario());
            comentario.setTask(comentario1.getTask());
            comentarioPersist.set(createOrUpdate(comentario));
        });
        return comentarioPersist.get();
    }

}
