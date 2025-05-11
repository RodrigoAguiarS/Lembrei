package com.rodrigo.lembrei.service.impl;

import com.rodrigo.lembrei.data.Categoria;
import com.rodrigo.lembrei.repository.CategoriaRepository;
import com.rodrigo.lembrei.service.CategoriaService;

import java.util.List;

public class CategoriaServiceImpl implements CategoriaService {
    private final CategoriaRepository repository;

    public CategoriaServiceImpl(CategoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Categoria salvar(Categoria categoria) {
        if (categoria.getId() == null) {
            long id = repository.inserir(categoria);
            categoria.setId(id);
        } else {
            repository.atualizar(categoria);
        }
        return categoria;
    }

    @Override
    public List<Categoria> listarTodas() {
        return repository.buscarTodas();
    }

    @Override
    public Categoria buscarPorId(Long id) {
        return repository.buscarPorId(id);
    }

    @Override
    public void deletar(Long id) {
        repository.deletar(id);
    }
}
