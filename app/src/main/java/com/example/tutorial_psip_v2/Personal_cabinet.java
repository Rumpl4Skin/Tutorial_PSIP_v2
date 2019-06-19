package com.example.tutorial_psip_v2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;


public class Personal_cabinet extends Fragment {




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_cabinet, container, false);
        CircularProgressIndicator progressTasks = view.findViewById(R.id.progress_tasks);
        CircularProgressIndicator progressTest = view.findViewById(R.id.progress_tests);

// you can set max and current progress values individually

// or all at once
        progressTest.setProgress(5000, 10000);
        progressTasks.setProgress(5000, 10000);

// you can get progress values using following getters
        //circularProgress.getProgress(); // returns 5000
       // circularProgress.getMaxProgress(); // returns 10000
        return view;
    }

}
