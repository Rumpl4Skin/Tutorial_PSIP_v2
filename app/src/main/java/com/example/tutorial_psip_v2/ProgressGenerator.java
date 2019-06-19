package com.example.tutorial_psip_v2;

import android.os.AsyncTask;

import com.dd.processbutton.ProcessButton;

public class ProgressGenerator extends AsyncTask<Void, Integer, Void>{

    ProcessButton pb;

    public void start(ProcessButton pb){
        this.pb = pb;
        execute();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void[] args){

        try
        {
            for (int i=0; i < 101; i = i+20)
            {
                publishProgress(i);
                Thread.sleep(500);
            }
        }catch (InterruptedException e){}

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onProgressUpdate(Integer[] values){

        if (pb != null){
            pb.setProgress(values[0]);
        }
    }

}
