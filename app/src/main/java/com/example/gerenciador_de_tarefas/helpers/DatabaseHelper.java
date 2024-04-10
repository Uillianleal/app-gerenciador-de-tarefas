package com.example.gerenciador_de_tarefas.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gerenciador_de_tarefas.entities.Tarefa;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "gerenciador_tarefas.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABELA_TAREFAS = "tarefas";
    public static final String COLUNA_ID = "id";
    public static final String COLUNA_TITULO = "titulo";
    public static final String COLUNA_DESCRICAO = "descricao";
    public static final String COLUNA_DATA_ENTREGA = "data_entrega";
    public static final String COLUNA_CONCLUIDA = "concluida";
    public static final String COLUNA_PRIORIDADE = "prioridade";

    private static final String SQL_CRIAR_TAREFA =
            "CREATE TABLE " + TABELA_TAREFAS + " (" +
                    COLUNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUNA_TITULO + " TEXT," +
                    COLUNA_DESCRICAO + " TEXT," +
                    COLUNA_DATA_ENTREGA + " INTEGER," +
                    COLUNA_CONCLUIDA + " INTEGER," +
                    COLUNA_PRIORIDADE + " TEXT" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CRIAR_TAREFA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_TAREFAS);
        onCreate(db);
    }

    public long inserirTarefa(Tarefa tarefa) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(COLUNA_TITULO, tarefa.getTitulo());
            values.put(COLUNA_DESCRICAO, tarefa.getDescricao());
            values.put(COLUNA_DATA_ENTREGA, tarefa.getDataEntrega().getTime());
            values.put(COLUNA_CONCLUIDA, tarefa.isConcluida() ? 1 : 0);
            values.put(COLUNA_PRIORIDADE, tarefa.getPrioridade().toString());

            id = db.insert(TABELA_TAREFAS, null, values);
        }catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            db.close();
        }
        return id;
    }
}
