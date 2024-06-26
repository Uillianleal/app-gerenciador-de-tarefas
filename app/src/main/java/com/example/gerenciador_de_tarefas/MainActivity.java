package com.example.gerenciador_de_tarefas;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gerenciador_de_tarefas.adapters.TarefaAdapter;
import com.example.gerenciador_de_tarefas.entities.Tarefa;
import com.example.gerenciador_de_tarefas.helpers.DatabaseHelper;
import com.example.gerenciador_de_tarefas.helpers.LembreteReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int NEW_TASK_REQUEST_CODE = 1;
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private TarefaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        databaseHelper = new DatabaseHelper(this);
        atualizarListaTarefas();

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);
                startActivityForResult(intent, NEW_TASK_REQUEST_CODE);
            }
        });

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("Nova Tarefa")) {
                    Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);
                    startActivityForResult(intent, NEW_TASK_REQUEST_CODE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        canalDeNoticacao();
    }

    private void canalDeNoticacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nome = "Lembrete de Tarefas";
            String descricao = "Canal para noficações de lembretes de tarefas";
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel canal = new NotificationChannel("ID_CANAL_TAREFAS", nome, importancia);
            canal.setDescription(descricao);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(canal);
        }
    }

    private void atualizarListaTarefas() {
        List<Tarefa> tarefasList = databaseHelper.buscarTarefas();
        if (adapter == null) {
            adapter = new TarefaAdapter(tarefasList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setTarefas(tarefasList);
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        atualizarListaTarefas();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && (data.getBooleanExtra("tarefaInserida", false)
                    || data.getBooleanExtra("tarefaAtualizada", false))) {
                atualizarListaTarefas();
            }
        }
    }
}