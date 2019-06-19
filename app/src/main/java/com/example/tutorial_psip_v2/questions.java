package com.example.tutorial_psip_v2;

public class questions {
     int id_questions;
     ansvers[] ansvers;
     int id_test;
     String sod_question;
    public questions(int id_questions,ansvers[] ansvers,int id_test,String sod_question){
        this.id_questions=id_questions;
        this.ansvers=ansvers;
        this.id_test=id_test;
        this.sod_question=sod_question;

    }
    public questions(int id_questions,int id_test,String sod_question){
        this.id_questions=id_questions;
        this.id_test=id_test;
        this.sod_question=sod_question;
    }

    public void setAnsvers(com.example.tutorial_psip_v2.ansvers[] ansvers) {
        this.ansvers = ansvers;
    }
}
