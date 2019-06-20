package com.example.tutorial_psip_v2;


import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.IOException;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;


public class Personal_cabinet extends Fragment implements View.OnClickListener {
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    TextView user_mail, user_name,mail_user,name_user;
    CircularProgressIndicator progressTest,progressTasks;
    NavController navController;
    Button del_progress,del_acc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_cabinet, container, false);
         progressTasks = view.findViewById(R.id.progress_tasks);
         progressTest = view.findViewById(R.id.progress_tests);
         navController = Navigation.findNavController(getActivity(), R.id.fragment);
        del_progress = view.findViewById(R.id.btnDelProgress);
        del_acc = view.findViewById(R.id.btnDelAcc);
        del_acc.setOnClickListener(this);
        del_progress.setOnClickListener(this);


        progressTest.setProgress(get_progress_tests(), get_all_tests());
        progressTasks.setProgress(get_progress_tasks(), get_progress_tasks());

        user_name = view.findViewById(R.id.first_name);
        user_mail = view.findViewById(R.id.mail);
        name_user=getActivity().findViewById(R.id.name_user);
        user_name.setText(name_user.getText());
        user_mail.setText(mail_user.getText());

        return view;
    }
    public int get_all_tests(){
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
        Cursor cursor=mDb.rawQuery("select count(*) from test",null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public int get_progress_tasks(){
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
        mail_user=getActivity().findViewById(R.id.mail_user);
        Cursor cursor=mDb.rawQuery("select count(*) from progress_task where id_user=(select id_user from user where mail='"+mail_user.getText()+"')",null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public int get_progress_tests(){
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
        mail_user=getActivity().findViewById(R.id.mail_user);
        Cursor cursor=mDb.rawQuery("select count(*) from progress_tests where id_user=(select id_user from user where mail='"+mail_user.getText()+"')",null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public void del_progress(){
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
        mDb.execSQL("delete from progress where id_user=(select id_user from user where mail='"+mail_user.getText()+"')");
        mDb.execSQL("delete from progress_task where id_user=(select id_user from user where mail='"+mail_user.getText()+"')");
        mDb.execSQL("delete from progress_tests where id_user=(select id_user from user where mail='"+mail_user.getText()+"')");
        Toast.makeText(getActivity(), "Ваш прогресс обнулен!",
                Toast.LENGTH_LONG).show();
        navController.navigate(R.id.main_page);
    }

    public  void del_Acc(){
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
        del_progress();
        mDb.execSQL("delete from user where  mail='"+mail_user.getText()+"'");
        name_user.setText("@string/nav_header_title");
        mail_user.setText("@string/nav_header_subtitle");
        Toast.makeText(getActivity(), "Аккаунт успешно удален!",
                Toast.LENGTH_LONG).show();
        navController.navigate(R.id.main_page);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.btnDelProgress:
                del_progress();
                break;
            case R.id.btnDelAcc:
                del_Acc();
                break;
        }
    }
}
