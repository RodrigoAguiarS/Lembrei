package com.rodrigo.lembrei.data;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Categoria implements Serializable {
    private Long id;
    private String nome;
    private String icone;
    private String corHex;

    public Categoria() {
    }

    public Categoria(Object o, String todas) {
    }

    @NonNull
    @Override
    public String toString() {
        return icone + " " + nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public String getCorHex() {
        return corHex;
    }

    public void setCorHex(String corHex) {
        this.corHex = corHex;
    }
}
