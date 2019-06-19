package com.example.tutorial_psip_v2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class StateAdapter extends ArrayAdapter<State> {

    private LayoutInflater inflater;
    private int layout;
    private List<State> states;

    public StateAdapter(Context context, int resource, List<State> states) {
        super(context, resource, states);
        this.states = states;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView=inflater.inflate(this.layout, parent, false);



        ImageView flagView = (ImageView) convertView.findViewById(R.id.img_lang);
        TextView nameView = (TextView) convertView.findViewById(R.id.name_lang);


        State state = states.get(position);

        flagView.setImageBitmap(state.getFlagResource());
        nameView.setText(state.getName());
    }
        return convertView;
    }
}
