package com.rodrigo.lembrei.service;

import com.rodrigo.lembrei.data.TipoTransacao;
import com.rodrigo.lembrei.data.Transacao;

import java.math.BigDecimal;
import java.util.List;

public interface TransacaoService {
    Transacao salvar(Transacao transacao);
    Transacao atualizar(Transacao transacao);
    void marcarComoPago(Transacao transacao);
    List<Transacao> listarTodas();
    Transacao buscarPorId(Long id);
    void deletar(Long id);
    List<Transacao> buscarPorTipo(TipoTransacao tipo);
    List<Transacao> listarPendentes();
    BigDecimal calcularTotalMes(TipoTransacao tipo);
    List<Transacao> listarProximasTransacoes();
    List<Transacao> listarItensVencidos();
}