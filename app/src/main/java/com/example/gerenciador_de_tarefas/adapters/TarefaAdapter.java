package com.example.gerenciador_de_tarefas.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gerenciador_de_tarefas.NewTaskActivity;
import com.example.gerenciador_de_tarefas.R;
import com.example.gerenciador_de_tarefas.entities.Tarefa;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TarefaAdapter extends RecyclerView.Adapter<TarefaAdapter.TarefaViewHolder> {
    private ImageView editIcon;
    private List<Tarefa> tarefasList;

    public TarefaAdapter(List<Tarefa> tarefasList) {
        this.tarefasList = tarefasList;
    }

    public void setTarefas(List<Tarefa> tarefasList) {
        this.tarefasList = tarefasList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TarefaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarefa, parent, false);
        return new TarefaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TarefaViewHolder holder, int position) {
        Tarefa tarefa = tarefasList.get(position);
        holder.bind(tarefa);
    }

    @Override
    public int getItemCount() {
        return tarefasList.size();
    }

    public class TarefaViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitulo;
        private TextView textViewDescricao;
        private TextView textViewDataEntrega;
        private ImageView imageViewPrioridade;
        private ImageView editIcon;

        public TarefaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewDescricao = itemView.findViewById(R.id.textViewDescricao);
            textViewDataEntrega = itemView.findViewById(R.id.textViewDataEntrega);
            imageViewPrioridade = itemView.findViewById(R.id.imageViewPrioridade);
            editIcon = itemView.findViewById(R.id.edit_icon);
        }

        public void bind(Tarefa tarefa) {
            textViewTitulo.setText(tarefa.getTitulo());
            textViewDescricao.setText(tarefa.getDescricao());

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dataEntregaFormatada = formatter.format(tarefa.getDataEntrega());
            textViewDataEntrega.setText(dataEntregaFormatada);

            switch (tarefa.getPrioridade()) {
                case LOW:
                    imageViewPrioridade.setImageResource(R.drawable.circle_green);
                    break;
                case MEDIUM:
                    imageViewPrioridade.setImageResource(R.drawable.circle_yellow);
                    break;
                case HIGH:
                    imageViewPrioridade.setImageResource(R.drawable.circle_red);
                    break;
            }

            editIcon.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), NewTaskActivity.class);
                intent.putExtra("TAREFA_ID", tarefa.getId());
                itemView.getContext().startActivity(intent);
            });
        }
    }
}