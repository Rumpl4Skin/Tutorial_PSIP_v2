package com.example.tutorial_psip_v2;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.dd.processbutton.iml.ActionProcessButton;

import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.highlight.ColorTheme;


public class seadbox extends Fragment {

   Button comp;
    WebView cod;
    EditText edtCod;
    CodeView codeView;
    String kod;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_seadbox, container, false);
        comp = v.findViewById(R.id.btnCompiling);
        codeView=v.findViewById(R.id.code_view);
        edtCod=v.findViewById(R.id.edtCod);
        cod=v.findViewById(R.id.code);


        comp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cod.getSettings().setJavaScriptEnabled(true);

                // указываем страницу загрузки
                //kod=kod+edtCod.getText().toString();
                //edtCod.setText("");
                codeView.setOptions(Options.Default.get(getActivity())
                        .withLanguage("css")
                        .withCode(edtCod.getText().toString())
                        .withTheme(ColorTheme.SOLARIZED_LIGHT));
                cod.loadData(edtCod.getText().toString(), "text/html", "UTF-8");

                Snackbar.make(v, "Генерируем страницу", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        });
        return v;
    }

}
