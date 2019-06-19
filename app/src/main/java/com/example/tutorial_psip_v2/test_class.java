package com.example.tutorial_psip_v2;

public class test_class {
    int id_test;
     String name_test; // название
     int id_uroka;
     boolean passed;
     questions[] questions;
     int time,attempt;

public  test_class(){
    this.id_test=0;
    this.name_test=null;
    this.id_uroka=0;
    this.passed=false;
    this.attempt=0;

}
    public test_class(int id_test,String name_test,int id_uroka,boolean passed,questions[] questions ){

        this.id_test=id_test;
        this.name_test=name_test;
        this.id_uroka=id_uroka;
        this.passed=passed;
        this.questions=questions;
        this.time=5000;
        this.attempt=0;
    }
    public test_class(int id_test,String name_test,int id_uroka,boolean passed ){

        this.id_test=id_test;
        this.name_test=name_test;
        this.id_uroka=id_uroka;
        this.passed=passed;
        this.questions=null;
        this.time=5000;
        this.attempt=0;
    }
    public int getId_test(){
        return  this.id_test;
    }

    public void setQuestions(com.example.tutorial_psip_v2.questions[] questions) {
        this.questions = questions;
    }
    /*   public String getName() {
        return this.name_lang;
    }

    public void setName(String name) {
        this.name_lang = name;
    }

    public android.graphics.Bitmap getFlagResource() {
        return this.flagResource;
    }

    public void setFlagResource(android.graphics.Bitmap flagResource) {
        this.flagResource = flagResource;
    }*/
    public boolean test_failed(){
        if(this.attempt==3||this.attempt>3)
            return true;
        else return false;
    }
}
