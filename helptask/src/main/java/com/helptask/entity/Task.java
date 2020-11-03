package com.helptask.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
public class Task {

    @Id
    private String id;

    private String titulo;

    @DBRef(lazy = true)
    private Usuario usuario;

    private Date data;

    private Integer number;

    private StatusEnum status;

    private PriorityEnum priority;

    @DBRef(lazy = true)
    private Usuario assigneredUser;

    private String description;

    private String image;

    @Transient
    private List<ChangeStatus> changeStatus;

    @Transient
    private List<Comentario> comentarios;

    private Date dataAgendamento;

    private String tempoGasto;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public PriorityEnum getPriority() {
        return priority;
    }

    public void setPriority(PriorityEnum priority) {
        this.priority = priority;
    }

    public Usuario getAssigneredUser() {
        return assigneredUser;
    }

    public void setAssigneredUser(Usuario assigneredUser) {
        this.assigneredUser = assigneredUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<ChangeStatus> getChangeStatus() {
        return changeStatus;
    }

    public void setChangeStatus(List<ChangeStatus> changeStatus) {
        this.changeStatus = changeStatus;
    }

    public Date getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(Date dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public String getTempoGasto() {
        return tempoGasto;
    }

    public void setTempoGasto(String tempoGasto) {
        this.tempoGasto = tempoGasto;
    }
}
