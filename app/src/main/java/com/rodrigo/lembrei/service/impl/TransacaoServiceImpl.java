package com.rodrigo.lembrei.service.impl;

import com.rodrigo.lembrei.data.TipoTransacao;
import com.rodrigo.lembrei.data.Transacao;
import com.rodrigo.lembrei.repository.TransacaoRepository;
import com.rodrigo.lembrei.service.TransacaoService;

import java.util.List;
import java.util.stream.Collectors;

public class TransacaoServiceImpl implements TransacaoService {
    private final TransacaoRepository repository;

    public TransacaoServiceImpl(TransacaoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Transacao salvar(Transacao transacao) {
        long id = repository.inserir(transacao);
        transacao.setId(id);
        return transacao;
    }

    @Override
    public List<Transacao> listarTodas() {
        return repository.buscarTodos();
    }

    @Override
    public Transacao buscarPorId(Long id) {
        return repository.buscarPorId(id);
    }

    @Override
    public void deletar(Long id) {
    }

    @Override
    public List<Transacao> buscarPorTipo(TipoTransacao tipo) {
        return repository.buscarTodos().stream()
                .filter(t -> t.getTipo() == tipo)
                .collect(Collectors.toList());
    }
}
