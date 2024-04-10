package com.example.gerenciador_de_tarefas;

import android.app.DatePickerDialog;
import android.os.Bundle;
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
                EditText editTextTitulo = findViewById(R.id.editTextText);
                EditText editTextDescricao = findViewById(R.id.editTextTaskDescription);
                EditText editTextData = findViewById(R.id.editTextTaskDate);

                String titulo = editTextTitulo.getText().toString();
                String descricao = editTextDescricao.getText().toString();
                String dataString = editTextData.getText().toString();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date dataEntrega = null;

                try {
                    dataEntrega = formatter.parse(dataString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                RadioButton rbLow = findViewById(R.id.radioButtonBaixo);
                RadioButton rbMedium = findViewById(R.id.radioButtonMedio);
                RadioButton rbHigh = findViewById(R.id.radioButtonAlto);

                Prioridade prioridade;

                if (rbLow.isChecked()) {
                    prioridade = Prioridade.LOW;
                } else if (rbMedium.isChecked()) {
                    prioridade = Prioridade.MEDIUM;
                } else {
                    prioridade = Prioridade.HIGH;
                }

                if (titulo.isEmpty() || dataEntrega == null) {
                    Toast.makeText(NewTaskActivity.this,
                            "Título e data são obrigatórios.", Toast.LENGTH_SHORT).show();
                } else {
                    Tarefa novaTarefa = new Tarefa(0, titulo, descricao, dataEntrega,
                            false, prioridade);

                    DatabaseHelper dpHelper = new DatabaseHelper(NewTaskActivity.this);
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
                    } else {
                        Toast.makeText(NewTaskActivity.this, "Erro ao inserir tarefa",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}