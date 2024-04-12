package com.example.gerenciador_de_tarefas;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gerenciador_de_tarefas.entities.Tarefa;
import com.example.gerenciador_de_tarefas.enums.Prioridade;
import com.example.gerenciador_de_tarefas.helpers.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewTaskActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent().hasExtra("TAREFA_ID")) {
            int tarefaId = getIntent().getIntExtra("TAREFA_ID", -1);
            if (tarefaId != -1) {
                carregarDadosTarefa(tarefaId);
            }
        }
        final EditText editTextTaskDate = findViewById(R.id.editTextTaskDate);
        editTextTaskDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                Locale locale = new Locale("pt", "BR");
                Locale.setDefault(locale);
                calendar.setFirstDayOfWeek(Calendar.SUNDAY);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        NewTaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                                editTextTaskDate.setText(selectedDate);
                            }
                        }, year, month, day);

                datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.SUNDAY);
                datePickerDialog.show();
            }
        });

        Button saveButton = findViewById(R.id.button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EditText editTextTitulo = findViewById(R.id.editTextText);
                    EditText editTextDescricao = findViewById(R.id.editTextTaskDescription);
                    EditText editTextData = findViewById(R.id.editTextTaskDate);

                    String titulo = editTextTitulo.getText().toString().trim();
                    String descricao = editTextDescricao.getText().toString().trim();
                    String dataString = editTextData.getText().toString().trim();

                    if (titulo.isEmpty() || dataString.isEmpty()) {
                        Toast.makeText(NewTaskActivity.this, "Título e data são obrigatórios.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date dataEntrega = formatter.parse(dataString);

                    RadioButton rbLow = findViewById(R.id.radioButtonBaixo);
                    RadioButton rbMedium = findViewById(R.id.radioButtonMedio);
                    RadioButton rbHigh = findViewById(R.id.radioButtonAlto);

                    Prioridade prioridade = rbLow.isChecked() ? Prioridade.LOW : rbMedium.isChecked()
                            ? Prioridade.MEDIUM : Prioridade.HIGH;

                    DatabaseHelper dpHelper = new DatabaseHelper(NewTaskActivity.this);
                    if (getIntent().hasExtra("TAREFA_ID")) {
                        //Codigo para a Edição de uma nova tarefa by:Uillian
                        int tarefaId = getIntent().getIntExtra("TAREFA_ID", -1);
                        Tarefa tarefaExistente = new Tarefa(tarefaId, titulo, descricao, dataEntrega,
                                false, prioridade);
                        int rowsAffected = dpHelper.atualizarTarefa(tarefaExistente);
                        if (rowsAffected > 0) {
                            Toast.makeText(NewTaskActivity.this, "Tarefa atualizada com sucesso!",
                                    Toast.LENGTH_SHORT).show();

                            editTextTitulo.setText("");
                            editTextDescricao.setText("");
                            editTextData.setText("");
                            rbLow.setChecked(false);
                            rbMedium.setChecked(false);
                            rbHigh.setChecked(false);

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("tarefaAtualizada", true);
                            setResult(RESULT_OK, resultIntent);
                            finish();

                        } else {
                            Toast.makeText(NewTaskActivity.this, "Erro ao atualizar tarefa",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Codigo para a inserção de uma nova tarefa by:Uillian
                        Tarefa novaTarefa = new Tarefa(0, titulo, descricao, dataEntrega, false, prioridade);
                        long id = dpHelper.inserirTarefa(novaTarefa);
                        if (id > 0) {
                            Toast.makeText(NewTaskActivity.this, "Tarefa inserida com sucesso!",
                                    Toast.LENGTH_SHORT).show();

                            editTextTitulo.setText("");
                            editTextDescricao.setText("");
                            editTextData.setText("");
                            rbLow.setChecked(false);
                            rbMedium.setChecked(false);
                            rbHigh.setChecked(false);

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("tarefaInserida", true);
                            setResult(RESULT_OK, resultIntent);
                            finish();

                        } else {
                            Toast.makeText(NewTaskActivity.this, "Erro ao inserir tarefa",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (ParseException e) {
                    Toast.makeText(NewTaskActivity.this, "Formato de data inválido.",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(NewTaskActivity.this, "Erro ao salvar tarefa: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

       private void carregarDadosTarefa(int id) {
        DatabaseHelper db = new DatabaseHelper(this);
        Tarefa tarefa = db.buscarTarefaPorId(id);
        if (tarefa != null) {
            EditText editTextTitulo = findViewById(R.id.editTextText);
            EditText editTextDescricao = findViewById(R.id.editTextTaskDescription);
            EditText editTextData = findViewById(R.id.editTextTaskDate);

            editTextTitulo.setText(tarefa.getTitulo());
            editTextDescricao.setText(tarefa.getDescricao());
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dataString = formatter.format(tarefa.getDataEntrega());
            editTextData.setText(dataString);


            RadioButton rbLow = findViewById(R.id.radioButtonBaixo);
            RadioButton rbMedium = findViewById(R.id.radioButtonMedio);
            RadioButton rbHigh = findViewById(R.id.radioButtonAlto);
            switch (tarefa.getPrioridade()) {
                case LOW:
                    rbLow.setChecked(true);
                    break;
                case MEDIUM:
                    rbMedium.setChecked(true);
                    break;
                case HIGH:
                    rbHigh.setChecked(true);
                    break;
            }
        }
    }
}