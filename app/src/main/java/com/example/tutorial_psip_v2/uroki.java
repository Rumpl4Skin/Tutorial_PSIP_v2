package com.example.tutorial_psip_v2;


import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.json.JSONArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;


public class uroki extends Fragment {

    ArrayList<HashMap<String, String>> productsList;
    HttpURLConnection conn;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "temi";
    private static final String TAG_PID = "id_tema";
    private static final String TAG_NAME = "name_temi";

    // тут будет хранится список продуктов
    JSONArray temi = null;
    ListView lv;
    String name_them;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uroki, container, false);
        // Hashmap for ListView
        productsList = new ArrayList<HashMap<String, String>>();

        // Загружаем продукты в фоновом потоке
        new uroki.LoadAllUroki().execute();

        // получаем ListView
        lv =(ListView)view.findViewById(R.id.list_ur);

        // на выбор одного темы
        // запускается
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                Bundle bundle = new Bundle();
                name_them=productsList.get(position).toString();
                name_them=name_them.substring(11, name_them.indexOf(",") );

                // State selectedState = (State)parent.getItemAtPosition(position);
                bundle.putString("name_uroka", ""+name_them);
                bundle.putString("name_them",  getArguments().getString("name_them"));
                bundle.putString("name_lang",  getArguments().getString("name_lang"));
                setArguments(bundle);
                // navController.navigate(R.id.action_thems_to_sod_uroka2, bundle);



                //отключение не пройденных уроков

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
                TextView user_mail;
                user_mail=getActivity().findViewById(R.id.mail_user);
                Cursor id_urok=mDb.rawQuery("SELECT id_uroka FROM urok where name_uroka='"+name_them+"'",null);
                id_urok.moveToFirst();
                Cursor id_usera=mDb.rawQuery("SELECT id_user FROM user where mail='"+user_mail.getText()+"'",null);
                id_usera.moveToFirst();
                Cursor cursor= mDb.rawQuery("SELECT name_uroka FROM urok where id_uroka=(select id_uroka from progress)", null);
                if(cursor.getCount()!=0 ){//если есть открытые элементы
                cursor.moveToFirst();
                while (!cursor.isAfterLast()){
                    if((name_them.equals(cursor.getString(0))&&(! user_mail.getText().equals(getString(R.string.nav_header_subtitle))))||position==0)//если элемент совпадает с элементом открытым или он первый
                    {
                       // mDb.execSQL("insert into progress(id_urok,id_user) values ("+id_urok.getInt(0)+"," +
                        //        id_usera.getInt(0) + ")");
                        Navigation.findNavController(view).navigate(R.id.soderjanie_uroka, bundle);
                    }
                   // else Toast.makeText(getContext(), "Данный урок вами еще не открыт завершите предыдущий!",
                    //        Toast.LENGTH_LONG).show();
                    cursor.moveToNext();
                }}
                else{//если открытых элементов нет
                    if(position==0)
                        Navigation.findNavController(view).navigate(R.id.soderjanie_uroka,bundle);
                    else Toast.makeText(getContext(), "Данный урок вами еще не открыт завершите предыдущий!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }
    class LoadAllUroki extends AsyncTask<String, String, String> {

        /**
         * Перед началом фонового потока Show Progress Dialog
         * */
        String name_temi=null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Загрузка продуктов. Подождите...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();*/
            Intent intent = getActivity().getIntent();
            name_temi=  getArguments().getString("name_them");
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
        }

        /**
         * Получаем все продукт из url
         * */
        protected String doInBackground(String... args) {

            Cursor cursor= mDb.rawQuery("SELECT * FROM urok where id_tema=(select id_tema from tema where name_temi='"+name_temi+"')", null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                // Создаем новый HashMap
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(TAG_PID, cursor.getString(0));
                map.put(TAG_NAME, cursor.getString(1));
                // добавляем HashList в ArrayList
                productsList.add(map);
                cursor.moveToNext();
            }



            cursor.close();

            return null;
        }

        /**
         * После завершения фоновой задачи закрываем прогрес диалог
         * **/
        protected void onPostExecute(String file_url) {
            // закрываем прогресс диалог после получение все продуктов
            // pDialog.dismiss();
            // обновляем UI форму в фоновом потоке

            /**
             * Обновляем распарсенные JSON данные в ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), productsList,
                    R.layout.list_item_thems, new String[] { TAG_PID,
                    TAG_NAME},
                    new int[] { R.id.id_them, R.id.name_them });
            // обновляем listview
            lv.setAdapter(adapter);

        }

    }
}

