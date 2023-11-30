package com.example.masathai;

import java.sql.Date;

public class Candidate {
    private int c_id;
    private String fullName;
    private String email;
    private String password;
    private String gender;
    private String nationality;
    private Date dob;

    public  Candidate(int c_id,String fullName, String gender, String nationality){
        this.fullName = fullName;
        this.gender = gender;
        this.nationality  = nationality;
        this.c_id = c_id;

    }
    public Candidate(String fullName, String gender, String nationality, Date dob ){
        this.fullName = fullName;
        this.gender = gender;
        this.nationality = nationality;
        this.dob = dob;
    }




    public int getC_id() {
        return c_id;
    }



    public void setC_id(int c_id) {
        this.c_id = c_id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }



}
