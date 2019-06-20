package com.example.tutorial_psip_v2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.IOException;


public class test extends Fragment {
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    TextView name_testa, question, current_quest, total_quest, attempt;
    Button proverit, next_btn;
    RadioGroup answers;
    RadioButton ansv1, ansv2, ansv3, ansv0, r_lang, r_temi;
    public test_class this_test = new test_class();
    int current, total;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        name_testa = view.findViewById(R.id.name_test);
        proverit = view.findViewById(R.id.proverka);
        next_btn = view.findViewById(R.id.next_quest_btn);
        answers = view.findViewById(R.id.answers);
        ansv0 = view.findViewById(R.id.ansv0);
        ansv1 = view.findViewById(R.id.ansv1);
        ansv2 = view.findViewById(R.id.ansv2);
        ansv3 = view.findViewById(R.id.ansv3);
        current_quest = view.findViewById(R.id.current_quest);
        total_quest = view.findViewById(R.id.total_quest);
        attempt = view.findViewById(R.id.attempt);

        question = view.findViewById(R.id.question);


        this_test = initialData();//заполнение теста

        Intent intent = getActivity().getIntent();
        name_testa.setText("Тест по: " + getArguments().getString("name_uroka"));

        current = 1;
        total = this_test.questions.length;
        current_quest.setText("" + current);
        total_quest.setText("" + total);

        question.setText(this_test.questions[0].sod_question);
        ansv0.setText(this_test.questions[0].ansvers[0].sod_ansv);
        ansv1.setText(this_test.questions[0].ansvers[1].sod_ansv);
        ansv2.setText(this_test.questions[0].ansvers[2].sod_ansv);
        ansv3.setText(this_test.questions[0].ansvers[3].sod_ansv);

        final RadioButton[] textViews = new RadioButton[4];
        textViews[0] = ansv0;
        textViews[1] = ansv1;
        textViews[2] = ansv2;
        textViews[3] = ansv3;
        proverit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textViews[righthAnsv(this_test, current)].isChecked()) {//верный ответ
                    proverit.setTextColor(Color.GREEN);
                    proverit.setText("Верно!");
                    if (current != total) {//следующий вопрос
                        next_btn.setVisibility(View.VISIBLE);
                        next_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                current++;
                                current_quest.setText("" + current);
                                question.setText("" + this_test.questions[current - 1].sod_question);//заполнение вопроса
                                for (int i = 0; i < this_test.questions[current - 1].ansvers.length; i++)
                                    textViews[i].setText(this_test.questions[current - 1].ansvers[i].sod_ansv);
                                next_btn.setVisibility(View.INVISIBLE);

                                proverit.setTextColor(Color.BLACK);
                                proverit.setText("ДАТЬ ОТВЕТ");
                            }
                        });
                    } else {//окончание теста
                        //Toast.makeText(getContext(), " Тест успешно пройден! ", Toast.LENGTH_SHORT).show();
//Получаем вид с файла prompt.xml, который применим для диалогового окна:
                        this_test.passed = true;
                        testPassed(this_test);
                        LayoutInflater li = LayoutInflater.from(getActivity());
                        final View promptsView = li.inflate(R.layout.prompt, null);
                        //Создаем AlertDialog
                        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getActivity());

                        //Настраиваем prompt.xml для нашего AlertDialog:
                        mDialogBuilder.setView(promptsView);

                        //Настраиваем сообщение в диалоговом окне:
                        mDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Перейти",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                promptsView.findViewById(R.id.r_spec);
                                                r_lang = promptsView.findViewById(R.id.r_uch_obr);
                                                r_temi = promptsView.findViewById(R.id.r_spec);

                                                if (r_lang.isChecked()) {//возвращение к языкам
                                                    NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                                                    navController.navigate(R.id.main_page);
                                                } else {//возвращение к темам
                                                    String name_temi;
                                                    Intent intent = getActivity().getIntent();
                                                    name_temi = getArguments().getString("name_lang");
                                                    NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("name_lang", name_temi);
                                                    navController.navigate(R.id.action_test_to_thems, bundle);
                                                }
                                            }
                                        })
                                .setNegativeButton("Отмена",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                                                String name_temi = null, name_uroka;
                                                Intent intent = getActivity().getIntent();
                                                name_temi = getArguments().getString("name_them");
                                                name_uroka = getArguments().getString("name_uroka");
                                                Bundle bundle = new Bundle();
                                                bundle.putString("name_them", "" + name_temi);
                                                bundle.putString("name_uroka", "" + name_uroka);
                                                navController.navigate(R.id.action_test_to_soderjanie_uroka, bundle);
                                            }
                                        });

                        //Создаем AlertDialog:
                        AlertDialog alertDialog = mDialogBuilder.create();

                        //и отображаем его:
                        alertDialog.show();


                    }
                } else {//не верный ответ
                    this_test.attempt++;
                    attempt.setText("" + this_test.attempt);
                    attempt.setTextColor(Color.RED);
                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            attempt.setTextColor(Color.BLACK);
                        }

                    }.start();
                    if (this_test.test_failed()) {
                        Toast.makeText(getContext(), "Тест провален! Перечитайте мaтериал!",
                                Toast.LENGTH_SHORT).show();
                        NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                        String name_temi = null, name_uroka;
                        Intent intent = getActivity().getIntent();
                        name_temi = getArguments().getString("name_them");
                        name_uroka = getArguments().getString("name_uroka");
                        Bundle bundle = new Bundle();
                        bundle.putString("name_them", "" + name_temi);
                        bundle.putString("name_uroka", "" + name_uroka);
                        navController.navigate(R.id.action_test_to_soderjanie_uroka, bundle);
                    } else {
                        proverit.setTextColor(Color.RED);

                        proverit.setText("Не верно!");

                        new CountDownTimer(2000, 1000) {

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {
                                proverit.setTextColor(Color.BLACK);
                                proverit.setText("ДАТЬ ОТВЕТ");
                            }

                        }.start();


                    }
                }
            }

        });

        return view;
    }

    public int righthAnsv(test_class test, int current) {
        for (int i = 0; i < test.questions[current - 1].ansvers.length; i++)
            if (test.questions[current - 1].ansvers[i].right)
                return i;
        int gg = 0;
        return gg;
    }

    public test_class initialData() {//формирует весь тест с вопросами и ответами
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
        Cursor cursor = mDb.rawQuery("SELECT * FROM test where id_uroka=(select id_uroka from urok where name_uroka='" + getArguments().getString("name_uroka") + "')", null);
        if (cursor.getCount() == 0) {//если теста нету
            NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
            String name_temi = null, name_uroka;
            Intent intent = getActivity().getIntent();
            name_temi = getArguments().getString("name_them");
            name_uroka = getArguments().getString("name_uroka");
            Bundle bundle = new Bundle();
            bundle.putString("name_them", "" + name_temi);
            bundle.putString("name_uroka", "" + name_uroka);
            Toast.makeText(getContext(), "Тест пока не добавлен, мы работаем над этим!",
                    Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.action_test_to_soderjanie_uroka, bundle);
            test_class test = new test_class();
            return test;
        } else {
            cursor.moveToFirst();
            test_class test;
            test = new test_class(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), isPassed(cursor.getInt(3)));
            cursor.close();
            cursor = mDb.rawQuery("SELECT * FROM questions where id_test='" + test.getId_test() + "'", null);
            cursor.moveToFirst();
            int i = 0;
            int id_quest;

            while (!cursor.isAfterLast()) {
                i++;

                cursor.moveToNext();
            }
            questions[] questions = new questions[i];
            cursor.moveToFirst();
            i = 0;
            while (!cursor.isAfterLast()) {
                questions[i] = new questions(cursor.getInt(0), cursor.getInt(1), cursor.getString(2));

                id_quest = cursor.getInt(0);
                int j = 0;
                Cursor ansvers_count = mDb.rawQuery("select * from ansvers where id_question=" + id_quest + "", null);
                while (!ansvers_count.isAfterLast()) {
                    j++;
                    ansvers_count.moveToNext();
                }
                ansvers_count.close();
                ansvers[] ansvers_for_this_quest = new ansvers[j - 1];
                j = 0;
                Cursor ansvers = mDb.rawQuery("select * from ansvers where id_question='" + id_quest + "'", null);
                ansvers.moveToFirst();
                while (!ansvers.isAfterLast()) {
                    ansvers_for_this_quest[j] = new ansvers(ansvers.getInt(0), ansvers.getString(1), isPassed(ansvers.getInt(2)), ansvers.getInt(3));
                    j++;
                    ansvers.moveToNext();
                }
                questions[i].setAnsvers(ansvers_for_this_quest);
                i++;
                cursor.moveToNext();
            }
            cursor.close();
            test.setQuestions(questions);
            return test;
        }
    }

    public boolean isPassed(int passed) {
        if (passed == 1)
            return true;
        else return false;
    }

    public void testPassed(test_class test) {
        mDBHelper = new DatabaseHelper(getActivity());
TextView user_mail;
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
        if(user_mail.getText().equals(getString(R.string.nav_header_subtitle))){//если никто не авторизован
            Toast.makeText(getContext(), "Вы не авторизованы, прогресс не сохранится!",
                    Toast.LENGTH_LONG).show();
        }
        else {
            Cursor cursor=mDb.rawQuery("select * from user where auth=1",null);
            cursor.moveToFirst();
            if(cursor.getCount()!=0){
            //если кто-то входил
                cursor.close();
                cursor=mDb.rawQuery("select id_user from user where mail='" + user_mail.getText().toString() + "'",null);
                cursor.moveToFirst();
                int id_user=cursor.getInt(0);
                cursor.close();
            mDb.execSQL("insert into progress_tests(id_user,id_test) values ("+id_user+"," +
                    test.id_test + ")");
            }
        }
    }
}
