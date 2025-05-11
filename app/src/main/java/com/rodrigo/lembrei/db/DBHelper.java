package com.rodrigo.lembrei.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "lembrei.db";
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        criarTabelaCategorias(db);
        criarTabelaTransacoes(db);
    }

    private void criarTabelaTransacoes(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE transacoes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "titulo TEXT NOT NULL, " +
                        "tipo TEXT NOT NULL, " +
                        "valor DECIMAL(10,2) NOT NULL, " +
                        "data_vencimento DATE NOT NULL, " +
                        "frequencia TEXT NOT NULL, " +
                        "observacoes TEXT, " +
                        "categoria_id INTEGER, " +
                        "configuracao_lembrete_id INTEGER, " +
                        "pago INTEGER DEFAULT 0, " +
                        "parcelado INTEGER DEFAULT 0, " +
                        "total_parcelas INTEGER DEFAULT 0, " +
                        "numero_parcela INTEGER DEFAULT 0, " +
                        "FOREIGN KEY(categoria_id) REFERENCES categorias(id), " +
                        "FOREIGN KEY(configuracao_lembrete_id) REFERENCES configuracao_lembrete(id))"
        );
    }

    private void criarTabelaCategorias(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE categorias (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nome TEXT NOT NULL, " +
                        "icone TEXT NOT NULL, " +
                        "cor_hex TEXT NOT NULL)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS transacoes");
        db.execSQL("DROP TABLE IF EXISTS categorias");
        // db.execSQL("DROP TABLE IF EXISTS categorias");
        // db.execSQL("DROP TABLE IF EXISTS configuracao_lembrete");
        onCreate(db);
    }
}
