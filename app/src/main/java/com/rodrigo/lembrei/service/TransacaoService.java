package com.rodrigo.lembrei.service;

import com.rodrigo.lembrei.data.TipoTransacao;
import com.rodrigo.lembrei.data.Transacao;

import java.util.List;

public interface TransacaoService {
    Transacao salvar(Transacao transacao);
    List<Transacao> listarTodas();
    Transacao buscarPorId(Long id);
    void deletar(Long id);
    List<Transacao> buscarPorTipo(TipoTransacao tipo);
}