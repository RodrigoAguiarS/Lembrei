package com.rodrigo.lembrei.service;


import com.rodrigo.lembrei.data.Categoria;

import java.util.List;

public interface CategoriaService {
    Categoria salvar(Categoria categoria);
    List<Categoria> listarTodas();
    Categoria buscarPorId(Long id);
    void deletar(Long id);
}