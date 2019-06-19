package com.example.tutorial_psip_v2;


import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class main_page extends Fragment {
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private List<State> states = new ArrayList();

    ListView countriesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);
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

        Cursor cursor = mDb.rawQuery("SELECT * FROM language", null);
        cursor.moveToFirst();
        states.clear();
        while (!cursor.isAfterLast()) {
            states.add(new State(cursor.getString(1), BitmapFactory.decodeByteArray(cursor.getBlob(2), 0, cursor.getBlob(2).length)));
            cursor.moveToNext();
        }
        cursor.close();
        // получаем элемент ListView
        countriesList = (ListView) view.findViewById(R.id.countriesList);

        if(countriesList.getChildCount()==0) {
            // создаем адаптер
            StateAdapter stateAdapter = new StateAdapter(getContext(), R.layout.list_item, states);
            // устанавливаем адаптер
            countriesList.setAdapter(stateAdapter);
            // слушатель выбора в списке
            AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                    // получаем выбранный пункт
                    State selectedState = (State) parent.getItemAtPosition(position);
                    Toast.makeText(getContext(), "Был выбран пункт " + selectedState.getName(),
                            Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                    Bundle bundle = new Bundle();
                    bundle.putString("name_lang", "" + selectedState.getName());
                    navController.navigate(R.id.thems, bundle);

                }
            };
            countriesList.setOnItemClickListener(itemListener);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
