package com.example.tutorial_psip_v2;


import android.Manifest;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.content.Context.CLIPBOARD_SERVICE;

public class QR_reader extends Fragment implements ZXingScannerView.ResultHandler {
    Dialog myDialog;

    ZXingScannerView ScannerView;

    public static String txt;

    TextView resultt;
    Button btn_copy, open, open_app, new_scan;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_reader, container, false);

        ScannerView = view.findViewById(R.id.qr);
        getActivity().setContentView(ScannerView);

        String permission = Manifest.permission.CAMERA;
        int grant = ContextCompat.checkSelfPermission(getContext(), permission);
        //if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(getActivity(), permission_list, 1);
      //  }

       /*/ myDialog = new Dialog(getContext());
        myDialog.setContentView(R.layout.pop);

        resultt = (TextView) myDialog.findViewById(R.id.resultt);

        btn_copy = (Button) myDialog.findViewById(R.id.btn_copy);
        open = (Button) myDialog.findViewById(R.id.open);
        open_app = (Button) myDialog.findViewById(R.id.open_app);
        new_scan = (Button) myDialog.findViewById(R.id.new_scan);*/


        return view;
    }

    @Override
    public void handleResult(final Result result) {
        resultt.setText(result.getText());
      //  myDialog.getWindow().setBackgroundDrawableResource(R.color.colorAccentttt);
        myDialog.show();
        btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt = resultt.getText().toString();
                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                clipboardManager.setText(txt);
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getText().toString()));
                startActivity(browserIntent);
                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(result.getText().toString())));
            }
        });

        /*open_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScanCodActivity.this, MainActivity.class);
                intent.putExtra("link", resultt.getText().toString());
                startActivity(intent);
            }
        });*/

      /*  new_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostResume();
                myDialog.hide();
            }
        });
    }
  /*  @Override
    protected void onPause() {
        super.onPause();
        ScannerView.stopCamera();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        ScannerView.setResultHandler(this);
        ScannerView.startCamera();
   */ }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(),"Разрешение получено", Toast.LENGTH_SHORT).show();
                // perform your action here

            } else {
                Toast.makeText(getActivity(),"Разрешение не предоставлено", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
