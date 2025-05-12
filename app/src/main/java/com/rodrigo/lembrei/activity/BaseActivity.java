package com.rodrigo.lembrei.activity;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rodrigo.lembrei.R;
import com.rodrigo.lembrei.activity.categoria.CadastroCategoriaActivity;
import com.rodrigo.lembrei.activity.categoria.EditarCategoriaActivity;
import com.rodrigo.lembrei.activity.categoria.ListaCategoriasActivity;
import com.rodrigo.lembrei.activity.dashboard.DashboardActivity;
import com.rodrigo.lembrei.activity.transacao.CadastroTransacaoActivity;
import com.rodrigo.lembrei.activity.transacao.DetalhesTransacaoActivity;
import com.rodrigo.lembrei.activity.transacao.EditarTransacaoActivity;
import com.rodrigo.lembrei.activity.transacao.ListaTransacoesActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected void configurarBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

        String className = this.getClass().getSimpleName();
        if (className.equals(DashboardActivity.class.getSimpleName())) {
            bottomNav.setSelectedItemId(R.id.navigation_dashboard);
        } else if (className.equals(ListaTransacoesActivity.class.getSimpleName()) ||
                className.equals(CadastroTransacaoActivity.class.getSimpleName()) ||
                className.equals(EditarTransacaoActivity.class.getSimpleName()) ||
                className.equals(DetalhesTransacaoActivity.class.getSimpleName())) {
            bottomNav.setSelectedItemId(R.id.navigation_transacoes);
        } else if (className.equals(ListaCategoriasActivity.class.getSimpleName()) ||
                className.equals(CadastroCategoriaActivity.class.getSimpleName()) ||
                className.equals(EditarCategoriaActivity.class.getSimpleName())) {
            bottomNav.setSelectedItemId(R.id.navigation_categorias);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == bottomNav.getSelectedItemId()) {
                return true;
            }

            Intent intent = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_dashboard && !className.equals(DashboardActivity.class.getSimpleName())) {
                intent = new Intent(this, DashboardActivity.class);
            } else if (itemId == R.id.navigation_transacoes && !className.equals(ListaTransacoesActivity.class.getSimpleName())) {
                intent = new Intent(this, ListaTransacoesActivity.class);
            } else if (itemId == R.id.navigation_categorias && !className.equals(ListaCategoriasActivity.class.getSimpleName())) {
                intent = new Intent(this, ListaCategoriasActivity.class);
            }

            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }
}