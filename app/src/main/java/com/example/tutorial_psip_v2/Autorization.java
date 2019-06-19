package com.example.tutorial_psip_v2;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.dd.processbutton.iml.ActionProcessButton;

import java.io.IOException;


public class Autorization extends Fragment {

    ActionProcessButton btnSignIn;
    ProgressGenerator progressGenerator;
    EditText mail,pass;
    TextView registr;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    NavController navController;
    TextView name_user,mail_user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_autorization, container, false);

        btnSignIn = view.findViewById(R.id.btnSignInAPB);
        mail = view.findViewById(R.id.mail);
        pass = view.findViewById(R.id.pass);
        registr=view.findViewById(R.id.reg);

        navController = Navigation.findNavController(getActivity(), R.id.fragment);
        registr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.registration);
            }
        });
        progressGenerator = new ProgressGenerator();
         btnSignIn.setMode(ActionProcessButton.Mode.PROGRESS);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnSignIn.setEnabled(false);
                mail.setEnabled(false);
                pass.setEnabled(false);

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
                Cursor cursor= mDb.rawQuery("SELECT * FROM user where mail='"+mail.getText()+"'and pass='"+pass.getText()+"'", null);
                cursor.moveToFirst();
                if(cursor.getCount()>0){
                progressGenerator.start(btnSignIn);
                navController.navigate(R.id.main_page);
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                    name_user=getActivity().findViewById(R.id.name_user);
                    mail_user=getActivity().findViewById(R.id.mail_user);
                    name_user.setText(cursor.getString(1)+" "+cursor.getString(2));
                    mail_user.setText(cursor.getString(3));
                    cursor.close();
                    mDb.execSQL("UPDATE user set auth=1 where mail='"+mail.getText()+"'and pass='"+pass.getText()+"'");
                    Toast.makeText(getContext(), "Вы успешно вошли!",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    btnSignIn.setEnabled(true);
                    mail.setEnabled(true);
                    pass.setEnabled(true);
                    btnSignIn.setErrorText("Пользователль не найден!");
                    btnSignIn.setProgress(-1);
                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            btnSignIn.setProgress(0);
                        }

                    }.start();
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
