package com.rodrigo.lembrei.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rodrigo.lembrei.data.Categoria;
import com.rodrigo.lembrei.data.Frequencia;
import com.rodrigo.lembrei.data.TipoTransacao;
import com.rodrigo.lembrei.data.Transacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CategoriaRepository {
    private final SQLiteDatabase db;
    private static final String TABELA = "categorias";

    public CategoriaRepository(SQLiteDatabase db) {
        this.db = db;
    }

    public long inserir(Categoria categoria) {
        ContentValues valores = new ContentValues();
        valores.put("nome", categoria.getNome());
        valores.put("icone", categoria.getIcone());
        valores.put("cor_hex", categoria.getCorHex());
        return db.insert(TABELA, null, valores);
    }

    public List<Categoria> buscarTodas() {
        List<Categoria> categorias = new ArrayList<>();
        Cursor cursor = db.query(TABELA, null, null, null, null, null, "nome");

        while (cursor.moveToNext()) {
            categorias.add(criarCategoria(cursor));
        }
        cursor.close();
        return categorias;
    }

    public Categoria buscarPorId(Long id) {
        Cursor cursor = db.query(TABELA, null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {
            return criarCategoria(cursor);
        }
        cursor.close();
        return null;
    }

    public int atualizar(Categoria categoria) {
        ContentValues valores = new ContentValues();
        valores.put("nome", categoria.getNome());
        valores.put("icone", categoria.getIcone());
        valores.put("cor_hex", categoria.getCorHex());

        return db.update(TABELA, valores, "id = ?",
                new String[]{String.valueOf(categoria.getId())});
    }

    public void deletar(Long id) {
        db.delete(TABELA, "id = ?", new String[]{String.valueOf(id)});
    }

    private Categoria criarCategoria(Cursor cursor) {
        Categoria categoria = new Categoria();
        categoria.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
        categoria.setNome(cursor.getString(cursor.getColumnIndexOrThrow("nome")));
        categoria.setIcone(cursor.getString(cursor.getColumnIndexOrThrow("icone")));
        categoria.setCorHex(cursor.getString(cursor.getColumnIndexOrThrow("cor_hex")));
        return categoria;
    }
}
