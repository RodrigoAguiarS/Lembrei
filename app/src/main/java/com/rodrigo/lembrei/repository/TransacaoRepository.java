package com.rodrigo.lembrei.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rodrigo.lembrei.data.Frequencia;
import com.rodrigo.lembrei.data.TipoTransacao;
import com.rodrigo.lembrei.data.Transacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransacaoRepository {
    private final SQLiteDatabase db;
    private static final String TABELA = "transacoes";

    public TransacaoRepository(SQLiteDatabase db) {
        this.db = db;
    }

    public long inserir(Transacao transacao) {
        ContentValues valores = new ContentValues();
        valores.put("titulo", transacao.getTitulo());
        valores.put("tipo", transacao.getTipo().name());
        valores.put("valor", transacao.getValor().toString());
        valores.put("data_vencimento", transacao.getDataVencimento().toString());
        valores.put("frequencia", transacao.getFrequencia().name());
        valores.put("observacoes", transacao.getObservacoes());
        valores.put("categoria_id", transacao.getCategoriaId());
        valores.put("configuracao_lembrete_id", transacao.getConfiguracaoLembreteId());
        valores.put("pago", transacao.isPago() ? 1 : 0);
        valores.put("parcelado", transacao.isParcelado() ? 1 : 0);
        valores.put("total_parcelas", transacao.getTotalParcelas());
        valores.put("numero_parcela", transacao.getNumeroParcela());
        return db.insert(TABELA, null, valores);
    }

    public void marcarComoPago(Transacao transacao) {
        ContentValues valores = new ContentValues();
        valores.put("pago", transacao.isPago() ? 1 : 0);

        db.update(TABELA, valores, "id = ?",
                new String[]{String.valueOf(transacao.getId())});
    }

    public List<Transacao> buscarPendentes() {
        List<Transacao> transacoes = new ArrayList<>();
        Cursor cursor = db.query(
                TABELA,
                null,
                "pago = ?",
                new String[]{"0"},
                null,
                null,
                "data_vencimento ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                transacoes.add(criarTransacao(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transacoes;
    }

    public List<Transacao> buscarTodos() {
        List<Transacao> transacoes = new ArrayList<>();
        Cursor cursor = db.query(TABELA, null, null, null, null, null, "data_vencimento");

        while (cursor.moveToNext()) {
            transacoes.add(criarTransacao(cursor));
        }
        cursor.close();
        return transacoes;
    }

    public void atualizar(Transacao transacao) {
        ContentValues valores = new ContentValues();
        valores.put("titulo", transacao.getTitulo());
        valores.put("tipo", transacao.getTipo().name());
        valores.put("valor", transacao.getValor().toString());
        valores.put("data_vencimento", transacao.getDataVencimento().toString());
        valores.put("frequencia", transacao.getFrequencia().name());
        valores.put("observacoes", transacao.getObservacoes());
        valores.put("categoria_id", transacao.getCategoriaId());
        valores.put("configuracao_lembrete_id", transacao.getConfiguracaoLembreteId());
        valores.put("pago", transacao.isPago() ? 1 : 0);
        valores.put("parcelado", transacao.isParcelado() ? 1 : 0);
        valores.put("total_parcelas", transacao.getTotalParcelas());
        valores.put("numero_parcela", transacao.getNumeroParcela());

        db.update(TABELA, valores, "id = ?",
                new String[]{String.valueOf(transacao.getId())});
    }

    public Transacao buscarPorId(Long id) {
        Cursor cursor = db.query(TABELA, null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {
            return criarTransacao(cursor);
        }
        cursor.close();
        return null;
    }

    public void deletar(Long id) {
        db.delete(TABELA, "id = ?", new String[]{String.valueOf(id)});
    }

    private Transacao criarTransacao(Cursor cursor) {
        Transacao transacao = new Transacao();
        transacao.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
        transacao.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow("titulo")));
        transacao.setTipo(TipoTransacao.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("tipo"))));
        transacao.setValor(new BigDecimal(cursor.getString(cursor.getColumnIndexOrThrow("valor"))));
        transacao.setDataVencimento(LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow("data_vencimento"))));
        transacao.setFrequencia(Frequencia.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("frequencia"))));
        transacao.setObservacoes(cursor.getString(cursor.getColumnIndexOrThrow("observacoes")));
        transacao.setCategoriaId(cursor.getLong(cursor.getColumnIndexOrThrow("categoria_id")));
        transacao.setConfiguracaoLembreteId(cursor.getLong(cursor.getColumnIndexOrThrow("configuracao_lembrete_id")));
        transacao.setPago(cursor.getInt(cursor.getColumnIndexOrThrow("pago")) == 1);
        transacao.setParcelado(cursor.getInt(cursor.getColumnIndexOrThrow("parcelado")) == 1);
        transacao.setTotalParcelas(cursor.getInt(cursor.getColumnIndexOrThrow("total_parcelas")));
        transacao.setNumeroParcela(cursor.getInt(cursor.getColumnIndexOrThrow("numero_parcela")));

        return transacao;
    }

    public List<Transacao> buscarVencidos(LocalDate dataReferencia) {
        List<Transacao> transacoes = new ArrayList<>();

        Cursor cursor = db.query(
                TABELA,
                null,
                "data_vencimento < ? AND pago = ?",
                new String[]{
                        dataReferencia.toString(),
                        "0"
                },
                null,
                null,
                "data_vencimento ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                transacoes.add(criarTransacao(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transacoes;
    }

    public List<Transacao> buscarPorPeriodo(LocalDate inicio, LocalDate fim, Boolean pago) {
        List<Transacao> transacoes = new ArrayList<>();
        String selecao;
        String[] argumentos;

        if (pago != null) {
            selecao = "data_vencimento BETWEEN ? AND ? AND pago = ?";
            argumentos = new String[]{
                    inicio.toString(),
                    fim.toString(),
                    pago ? "1" : "0"
            };
        } else {
            selecao = "data_vencimento BETWEEN ? AND ?";
            argumentos = new String[]{
                    inicio.toString(),
                    fim.toString()
            };
        }

        Cursor cursor = db.query(
                TABELA,
                null,
                selecao,
                argumentos,
                null,
                null,
                "data_vencimento ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                transacoes.add(criarTransacao(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transacoes;
    }

    public BigDecimal somarTransacoesPorPeriodo(TipoTransacao tipo, LocalDate inicio, LocalDate fim) {
        String query = "SELECT SUM(CAST(valor AS DECIMAL)) FROM " + TABELA +
                " WHERE tipo = ? AND data_vencimento BETWEEN ? AND ?";

        Cursor cursor = db.rawQuery(
                query,
                new String[]{
                        tipo.name(),
                        inicio.toString(),
                        fim.toString()
                }
        );

        BigDecimal total = BigDecimal.ZERO;
        if (cursor.moveToFirst()) {
            String valorTotal = cursor.getString(0);
            if (valorTotal != null) {
                total = new BigDecimal(valorTotal);
            }
        }
        cursor.close();
        return total;
    }
}
