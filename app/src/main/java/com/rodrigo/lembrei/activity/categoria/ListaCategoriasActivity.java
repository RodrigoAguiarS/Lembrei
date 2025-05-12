package com.rodrigo.lembrei.activity.categoria;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rodrigo.lembrei.R;
import com.rodrigo.lembrei.activity.BaseActivity;
import com.rodrigo.lembrei.data.Categoria;
import com.rodrigo.lembrei.adapter.CategoriaAdapter;
import com.rodrigo.lembrei.db.DBHelper;
import com.rodrigo.lembrei.repository.CategoriaRepository;
import com.rodrigo.lembrei.service.CategoriaService;
import com.rodrigo.lembrei.service.impl.CategoriaServiceImpl;

public class ListaCategoriasActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private CategoriaAdapter adapter;
    private CategoriaService categoriaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_categorias);
        configurarBottomNavigation();

        inicializarComponentes();
        configurarRecyclerView();
        configurarFAB();
        configurarServicos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarCategorias();
    }

    private void inicializarComponentes() {
        recyclerView = findViewById(R.id.recyclerViewCategorias);
        FloatingActionButton fab = findViewById(R.id.fabAdicionarCategoria);
        fab.setOnClickListener(v -> abrirCadastroCategoria());
    }

    private void configurarRecyclerView() {
        adapter = new CategoriaAdapter();
        adapter.setOnItemClickListener(categoria -> {
            Toast.makeText(this, "Categoria: " + categoria.getNome(), Toast.LENGTH_SHORT).show();
        });

        adapter.setOnDeleteClickListener(this::confirmarDelecao);

        adapter.setOnEditClickListener(categoria -> {
            Intent intent = new Intent(this,
                    EditarCategoriaActivity.class);
            intent.putExtra("categoria", categoria);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void configurarServicos() {
        DBHelper dbHelper = new DBHelper(this);
        CategoriaRepository repository = new CategoriaRepository(dbHelper.getWritableDatabase());
        categoriaService = new CategoriaServiceImpl(repository);
    }

    private void configurarFAB() {
        FloatingActionButton fab = findViewById(R.id.fabAdicionarCategoria);
        fab.setOnClickListener(v -> abrirCadastroCategoria());
    }

    private void abrirCadastroCategoria() {
        Intent intent = new Intent(this, CadastroCategoriaActivity.class);
        startActivity(intent);
    }

    private void carregarCategorias() {
        adapter.atualizarLista(categoriaService.listarTodas());
    }

    private void confirmarDelecao(Categoria categoria) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar exclusão")
                .setMessage("Deseja realmente excluir a categoria " + categoria.getNome() + "?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    try {
                        categoriaService.deletar(categoria.getId());
                        Toast.makeText(this, "Categoria excluída com sucesso!",
                                Toast.LENGTH_SHORT).show();
                        carregarCategorias();
                    } catch (Exception e) {
                        Toast.makeText(this, "Erro ao excluir categoria: " +
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }
}
