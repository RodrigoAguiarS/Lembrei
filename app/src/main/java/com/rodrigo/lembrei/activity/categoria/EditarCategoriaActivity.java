package com.rodrigo.lembrei.activity.categoria;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.rodrigo.lembrei.R;
import com.rodrigo.lembrei.data.Categoria;
import com.rodrigo.lembrei.db.DBHelper;
import com.rodrigo.lembrei.repository.CategoriaRepository;
import com.rodrigo.lembrei.service.CategoriaService;
import com.rodrigo.lembrei.service.impl.CategoriaServiceImpl;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

public class EditarCategoriaActivity extends AppCompatActivity {
    private TextInputEditText edtNomeCategoria, edtIcone, edtCorHex;
    private View viewCorPreview;
    private Button btnSalvarCategoria;
    private MaterialButton btnSelecionarIcone;
    private CategoriaService categoriaService;
    private int corSelecionada = Color.WHITE;
    private Categoria categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_categoria);

        categoria = (Categoria) getIntent().getSerializableExtra("categoria");
        if (categoria == null) {
            Toast.makeText(this, "Erro ao carregar categoria", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        inicializarComponentes();
        configurarToolbar();
        configurarServicos();
        configurarBotaoSalvar();
        configurarSeletores();
        preencherDadosCategoria();
        configurarFAB();
    }

    private void inicializarComponentes() {
        edtNomeCategoria = findViewById(R.id.edtNomeCategoria);
        edtIcone = findViewById(R.id.edtIcone);
        edtCorHex = findViewById(R.id.edtCorHex);
        viewCorPreview = findViewById(R.id.viewCorPreview);
        btnSalvarCategoria = findViewById(R.id.btnSalvarCategoria);
        btnSelecionarIcone = findViewById(R.id.btnSelecionarIcone);
    }

    private void configurarFAB() {
        FloatingActionButton fabListaCategorias = findViewById(R.id.fabListaCategorias);
        fabListaCategorias.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListaCategoriasActivity.class);
            startActivity(intent);
        });
    }

    private void configurarToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Editar Categoria");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void preencherDadosCategoria() {
        edtNomeCategoria.setText(categoria.getNome());
        edtIcone.setText(categoria.getIcone());
        edtCorHex.setText(categoria.getCorHex());
        viewCorPreview.setBackgroundColor(Color.parseColor(categoria.getCorHex()));
        corSelecionada = Color.parseColor(categoria.getCorHex());
    }

    private void configurarSeletores() {
        viewCorPreview.setOnClickListener(v -> mostrarSeletorCor());
        edtCorHex.setOnClickListener(v -> mostrarSeletorCor());
        btnSelecionarIcone.setOnClickListener(v -> mostrarSeletorIcone());
        edtIcone.setOnClickListener(v -> mostrarSeletorIcone());
    }

    private void mostrarSeletorCor() {
        new ColorPickerDialog.Builder(this)
                .setTitle("Selecione uma cor")
                .setPositiveButton("OK",
                        (ColorEnvelopeListener) (envelope, fromUser) -> {
                            corSelecionada = envelope.getColor();
                            viewCorPreview.setBackgroundColor(corSelecionada);
                            edtCorHex.setText("#" + envelope.getHexCode());
                        })
                .setNegativeButton("Cancelar",
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .attachBrightnessSlideBar(true)
                .setBottomSpace(12)
                .show();
    }

    private void mostrarSeletorIcone() {
        final String[] icones = {"📱", "💰", "🏠", "🚗", "✈️", "🍴", "🎮", "📚", "💊", "🎵"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha um ícone")
                .setItems(icones, (dialog, which) -> {
                    String iconeSelecionado = icones[which];
                    edtIcone.setText(iconeSelecionado);
                });
        builder.show();
    }

    private void configurarServicos() {
        DBHelper dbHelper = new DBHelper(this);
        CategoriaRepository repository = new CategoriaRepository(dbHelper.getWritableDatabase());
        categoriaService = new CategoriaServiceImpl(repository);
    }

    private void configurarBotaoSalvar() {
        btnSalvarCategoria.setOnClickListener(v -> salvarCategoria());
    }

    private void salvarCategoria() {
        try {
            String nome = edtNomeCategoria.getText() != null ?
                    edtNomeCategoria.getText().toString().trim() : "";
            String icone = edtIcone.getText() != null ?
                    edtIcone.getText().toString().trim() : "";
            String corHex = edtCorHex.getText() != null ?
                    edtCorHex.getText().toString().trim() : "";

            if (nome.isEmpty()) {
                edtNomeCategoria.setError("Nome obrigatório");
                return;
            }

            if (icone.isEmpty()) {
                edtIcone.setError("Ícone obrigatório");
                return;
            }

            if (corHex.isEmpty()) {
                edtCorHex.setError("Cor obrigatória");
                return;
            }

            categoria.setNome(nome);
            categoria.setIcone(icone);
            categoria.setCorHex(corHex);

            categoriaService.salvar(categoria);
            Toast.makeText(this, "Categoria atualizada com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao atualizar: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
