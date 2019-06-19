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
import android.widget.Button;

import androidx.navigation.Navigation;

import com.github.barteksc.pdfviewer.PDFView;

import org.json.JSONArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class soderjanie_uroka extends Fragment {


    ArrayList<HashMap<String, String>> urokList;
    HttpURLConnection conn;
    Button btnGoToTest,btnGoToTask;


    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "urok";
    private static final String TAG_PID = "id_uroka";
    private static final String TAG_NAME = "name_uroka";
    public static String name_urok;

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    // тут будет хранится список продуктов
    JSONArray temi = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }

PDFView webView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_soderjanie_uroka, container, false);
        webView=v.findViewById(R.id.web_sod_urok);

        urokList = new ArrayList<HashMap<String, String>>();
        btnGoToTest=v.findViewById(R.id.btnGoToTest);
        btnGoToTask=v.findViewById(R.id.btnGoToTask);
        btnGoToTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("name_uroka", ""+name_urok);
                bundle.putString("name_them",getArguments().getString("name_them"));
                bundle.putString("name_lang",getArguments().getString("name_lang"));

                Navigation.findNavController(view).navigate(R.id.test,bundle);
            }
        });
        btnGoToTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("name_uroka", ""+name_urok);
                bundle.putString("name_them",getArguments().getString("name_them"));
                bundle.putString("name_lang",getArguments().getString("name_lang"));
                // navController.navigate(R.id.action_thems_to_sod_uroka2, bundle);

                Navigation.findNavController(v).navigate(R.id.tasks,bundle);
            }
        });
        String name_temi,name_uroka;
        Intent intent = getActivity().getIntent();
        name_temi=  getArguments().getString("name_them");
        name_uroka=  getArguments().getString("name_uroka");
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
        Cursor cursor= mDb.rawQuery("SELECT * FROM urok where name_uroka='"+name_uroka+"' and id_tema=(select id_tema from tema where name_temi='"+name_temi+"')", null);
        cursor.moveToFirst();
            webView.fromBytes(cursor.getBlob(2)).load();
        Bundle bundle = new Bundle();
        bundle.putString("name_uroka",cursor.getString(1));
        name_urok=cursor.getString(1);
            cursor.close();
       // mDb.execSQL("UPDATE user set auth=1 where mail='"+mail.getText()+"'and pass='"+pass.getText()+"'");

        // обновляем listview
        cursor=mDb.rawQuery("select * from progress_tests where id_test=" +
                "(select id_test from test where id_uroka=(select id_uroka from urok where name_uroka='"+name_uroka+"'))",null);
        cursor.moveToFirst();
        if(cursor.getCount()>0){//проверка пройденности теста
            btnGoToTask.setEnabled(true);
        }
        else{//тест не пройден
            btnGoToTask.setEnabled(false);
        }
        return v;
    }

    }

