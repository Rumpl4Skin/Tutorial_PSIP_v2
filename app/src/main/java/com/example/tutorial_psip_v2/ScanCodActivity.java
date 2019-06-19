package com.example.tutorial_psip_v2;

import android.Manifest;
import android.app.Dialog;
import android.app.backup.BackupAgent;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanCodActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    Dialog myDialog;

    ZXingScannerView ScannerView;

    public static String txt;

    TextView resultt;
    Button btn_copy, open, open_app, new_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScannerView = new ZXingScannerView(this);
        setContentView(ScannerView);

        String permission = Manifest.permission.CAMERA;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }

            myDialog = new Dialog(this);
            myDialog.setContentView(R.layout.pop);

            resultt = (TextView) myDialog.findViewById(R.id.resultt);

            btn_copy = (Button) myDialog.findViewById(R.id.btn_copy);
            open = (Button) myDialog.findViewById(R.id.open);
            open_app = (Button) myDialog.findViewById(R.id.open_app);
            new_scan = (Button) myDialog.findViewById(R.id.new_scan);
    }

    @Override
    public void handleResult(final Result result) {

        resultt.setText(result.getText());


        Intent intent = new Intent(this,MainActivity.class);
        Intent intent1=getIntent();
         intent.putExtra("result", resultt.getText().toString());
        intent.putExtra("first_name", intent1.getStringExtra("first_name"));
        intent.putExtra("last_name", intent1.getStringExtra("last_name"));
        intent.putExtra("mail", intent1.getStringExtra("mail"));
        intent.putExtra("pass", intent1.getStringExtra("pass"));
        intent.putExtra("pin", intent1.getStringExtra("pin"));
        startActivity(intent);
        //navController = Navigation.findNavController(this, R.id.fragment);
       // navController.navigate(R.id.autorization);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScannerView.stopCamera();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        ScannerView.setResultHandler(this);
        ScannerView.startCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ScanCodActivity.this,"Разрешение получено", Toast.LENGTH_SHORT).show();
                // perform your action here

            } else {
                Toast.makeText(ScanCodActivity.this,"Разрешение не предоставлено", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
