package com.example.tutorial_psip_v2;


import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.IOException;


public class tasks extends Fragment {

TextView name_task,sod_task;
EditText edtTask;
Button check,generated;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    WebView gen_html;
    String right_ansv,name_uroka;
    int id_task;
    TextView user_mail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        name_task=view.findViewById(R.id.name_task);
        sod_task=view.findViewById(R.id.sod_task);
        edtTask=view.findViewById(R.id.edtTask);
        check=view.findViewById(R.id.btnCheck);
        generated=view.findViewById(R.id.btnGenerated);
        gen_html=view.findViewById(R.id.gen_html);
        Intent intent = getActivity().getIntent();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mDBHelper = new DatabaseHelper(getActivity());

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        name_uroka=getArguments().getString("name_uroka");
        Cursor cursor = mDb.rawQuery("SELECT * FROM tasks where id_uroka=(select id_uroka from urok where name_uroka='" +
                 name_uroka+ "')", null);
        if(cursor.getCount()!=0) {
            cursor.moveToFirst();
            id_task=cursor.getInt(0);
            name_task.setText(cursor.getString(1));
            sod_task.setText(cursor.getString(2));
            right_ansv=cursor.getString(5);
        }

        generated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gen_html.loadData(edtTask.getText().toString(), "text/html", "UTF-8");
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user_mail=getActivity().findViewById(R.id.mail_user);
                if(right_ansv.contains(edtTask.getText().toString())){//если ответ содержится
                   Cursor cursor=mDb.rawQuery("select id_user from user where mail='" + user_mail.getText().toString() + "'",null);
                    cursor.moveToFirst();
                    int id_user=cursor.getInt(0);
                    cursor.close();
                    if(!task_is_complited(id_task)) {//если задание еще не выполнял этот пользователь
                        mDb.execSQL("insert into progress_task(id_task, id_user)values(" + id_task +
                                "," + id_user + ")");
                        Toast.makeText(getContext(), "Вы справились с заданием!",
                                Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getContext(), "Вы справились с заданием снова!",
                                Toast.LENGTH_SHORT).show();

                    if(!urok_is_complited()) {//если урок еще не пройден этим пользователем
                        cursor = mDb.rawQuery("select id_uroka from urok where name_uroka='" +
                                name_uroka + "'", null);
                        cursor.moveToFirst();
                        mDb.execSQL("insert into progress(id_urok, id_user)values(" + cursor.getInt(0) +//урок выполнен
                                "," + id_user + ")");
                        cursor.close();
                    }
                }
                else {//если ответ не содержится
                    Toast.makeText(getContext(), "Вы не справились с заданием!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
    public boolean urok_is_complited(){
        mDBHelper = new DatabaseHelper(getActivity());

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        Cursor  cursor = mDb.rawQuery("select id_uroka from urok where name_uroka='" +
                name_uroka + "'", null);
        cursor.moveToFirst();
        int id_uroka=cursor.getInt(0);
        cursor.close();
        user_mail=getActivity().findViewById(R.id.mail_user);
         cursor=mDb.rawQuery("select * from progress where id_urok='"+id_uroka+"' and id_user='"+getUserId()+"'",null);
        cursor.moveToFirst();
        if(cursor.getCount()>0)
        return true;
        else return false;
    }

    public boolean task_is_complited(int id_task){
        mDBHelper = new DatabaseHelper(getActivity());

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        user_mail=getActivity().findViewById(R.id.mail_user);
        Cursor cursor=mDb.rawQuery("select * from progress_task where id_task='"+id_task+"'and id_user='"+getUserId()+"'",null);
        cursor.moveToFirst();
        if(cursor.getCount()>0)
            return true;
        else return false;
    }
    public int getUserId(){
        Cursor cursor=mDb.rawQuery("select id_user from user where mail='" + user_mail.getText().toString() + "'",null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

}
