package com.rodrigo.lembrei.data;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HistoricoPagamento {
    private Long id;
    private Long transacaoId;
    private LocalDate dataPagamento;
    private BigDecimal valorPago;
    private String observacoes;

    public HistoricoPagamento() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTransacaoId() {
        return transacaoId;
    }

    public void setTransacaoId(Long transacaoId) {
        this.transacaoId = transacaoId;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
