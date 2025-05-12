package com.rodrigo.lembrei.service.impl;

import com.rodrigo.lembrei.data.TipoTransacao;
import com.rodrigo.lembrei.data.Transacao;
import com.rodrigo.lembrei.repository.TransacaoRepository;
import com.rodrigo.lembrei.service.TransacaoService;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    public Transacao atualizar(Transacao transacao) {
        repository.atualizar(transacao);
        return repository.buscarPorId(transacao.getId());
    }

    @Override
    public BigDecimal calcularTotalMes(TipoTransacao tipo) {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fim = inicio.plusMonths(1).minusDays(1);
        return repository.somarTransacoesPorPeriodo(tipo, inicio, fim);
    }

    @Override
    public List<Transacao> listarProximasTransacoes() {
        LocalDate hoje = LocalDate.now();
        LocalDate proximoMes = hoje.plusMonths(1);
        return repository.buscarPorPeriodo(hoje, proximoMes, false);
    }

    @Override
    public List<Transacao> listarItensVencidos() {
        LocalDate hoje = LocalDate.now();
        return repository.buscarVencidos(hoje);
    }

    @Override
    public void marcarComoPago(Transacao transacao) {
        repository.marcarComoPago(transacao);
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
        repository.deletar(id);
    }

    @Override
    public List<Transacao> buscarPorTipo(TipoTransacao tipo) {
        return repository.buscarTodos().stream()
                .filter(t -> t.getTipo() == tipo)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transacao> listarPendentes() {
        return repository.buscarPendentes();
    }
}
