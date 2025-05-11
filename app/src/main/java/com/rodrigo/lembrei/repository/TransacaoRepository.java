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

    public List<Transacao> buscarTodos() {
        List<Transacao> transacoes = new ArrayList<>();
        Cursor cursor = db.query(TABELA, null, null, null, null, null, "data_vencimento");

        while (cursor.moveToNext()) {
            transacoes.add(criarTransacao(cursor));
        }
        cursor.close();
        return transacoes;
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
}
