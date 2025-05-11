package com.rodrigo.lembrei.data;

import java.time.LocalTime;

public class ConfiguracaoLembrete {
    private Long id;
    private Integer diasAntecedencia;
    private LocalTime horarioNotificacao;
    private Boolean persistenteAteConluir;

    public ConfiguracaoLembrete() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDiasAntecedencia() {
        return diasAntecedencia;
    }

    public void setDiasAntecedencia(Integer diasAntecedencia) {
        this.diasAntecedencia = diasAntecedencia;
    }

    public LocalTime getHorarioNotificacao() {
        return horarioNotificacao;
    }

    public void setHorarioNotificacao(LocalTime horarioNotificacao) {
        this.horarioNotificacao = horarioNotificacao;
    }

    public Boolean getPersistenteAteConluir() {
        return persistenteAteConluir;
    }

    public void setPersistenteAteConluir(Boolean persistenteAteConluir) {
        this.persistenteAteConluir = persistenteAteConluir;
    }
}
