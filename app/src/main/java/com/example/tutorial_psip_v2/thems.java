package com.example.tutorial_psip_v2;

import android.app.ProgressDialog;
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

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.json.JSONArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;


public class thems extends Fragment {
    private ProgressDialog pDialog;

    // Создаем JSON парсер


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

        View view = inflater.inflate(R.layout.fragment_thems, container, false);
        // Hashmap for ListView
        productsList = new ArrayList<HashMap<String, String>>();

        // Загружаем продукты в фоновом потоке
        new LoadAllProducts().execute();

        // получаем ListView
         lv =(ListView)view.findViewById(R.id.list);

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
                bundle.putString("name_them", ""+name_them);
                bundle.putString("name_lang",  getArguments().getString("name_lang"));
                setArguments(bundle);
               // navController.navigate(R.id.action_thems_to_sod_uroka2, bundle);

                Navigation.findNavController(view).navigate(R.id.uroki,bundle);

            }
        });

        return view;
    }
    class LoadAllProducts extends AsyncTask<String, String, String> {

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
           name_temi=  getArguments().getString("name_lang");
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







            Cursor cursor= mDb.rawQuery("SELECT * FROM tema where id_lang=(select id_lang from language where name_lang='"+name_temi+"')", null);
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
