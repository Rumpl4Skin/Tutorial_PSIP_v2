package com.example.tutorial_psip_v2;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import java.io.IOException;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    TextView name_user,mail_user;
    NavController navController;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    SwitchCompat s;

static{
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mail_user=findViewById(R.id.mail_user);



        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navController = Navigation.findNavController(this, R.id.fragment);
        Intent intent=getIntent();
        Bundle bundle = new Bundle();

        if(intent.getStringExtra("result")!=null) {
            bundle.putString("first_name",intent.getStringExtra("first_name"));
            bundle.putString("last_name",intent.getStringExtra("last_name"));
            bundle.putString("mail",intent.getStringExtra("mail"));
            bundle.putString("pass",intent.getStringExtra("pass"));
            bundle.putString("result",intent.getStringExtra("result"));
            bundle.putString("pin",intent.getStringExtra("pin"));
            navController.navigate(R.id.registration,bundle);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }
 public boolean isAuth()
 {
     mDBHelper = new DatabaseHelper(this);

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
     Cursor cursor= mDb.rawQuery("SELECT * FROM user where auth=1", null);
     cursor.moveToFirst();
     if(cursor.getCount()>0){
     name_user=findViewById(R.id.name_user);
     mail_user=findViewById(R.id.mail_user);
     name_user.setText(cursor.getString(1)+" "+cursor.getString(2));
     mail_user.setText(cursor.getString(3));
     return true;}
     else return false;
 }

 public void isLogOut(){
     mDBHelper = new DatabaseHelper(this);

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
     mDb.execSQL("update user set auth=0");
     Toast.makeText(this, "Вы вышли из учетной записи!",
             Toast.LENGTH_SHORT).show();
     name_user=findViewById(R.id.name_user);
     mail_user=findViewById(R.id.mail_user);
     name_user.setText(R.string.nav_header_title);
     mail_user.setText(R.string.nav_header_subtitle);
 }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.myswitch);
        item.setActionView(R.layout.switch_layout);
        name_user=findViewById(R.id.name_user);
        mail_user=findViewById(R.id.mail_user);
        isAuth();
        name_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.autorization);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        int id = item.getItemId();
            switch (id)
            {
                case R.id.action_login:
                    navController.navigate(R.id.autorization);
                    break;
                case R.id.action_logout:
                    isLogOut();
                    break;
                case R.id.myswitch:
                    int currentNightMode = getResources().getConfiguration().uiMode
                            & Configuration.UI_MODE_NIGHT_MASK;
                    switch (currentNightMode) {
                        case Configuration.UI_MODE_NIGHT_NO:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            recreate();
                            break;
                        case Configuration.UI_MODE_NIGHT_YES:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            recreate();
                            break;
                    }
                    break;
            }
            //скрытие клавиатуры
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        int id = item.getItemId();
        switch (id){
            case R.id.nav_camera:
                navController.navigate(R.id.main_page);
                break;
            case R.id.sand_box:
                navController.navigate(R.id.seadbox);
                break;
            case R.id.nav_send:
                if(mail_user.getText().equals(R.string.nav_header_subtitle)){
                navController.navigate(R.id.personal_cabinet);}
                else Toast.makeText(this, "Пока вы не авторизуетесь, этот раздел недоступен!",
                        Toast.LENGTH_LONG).show();
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
