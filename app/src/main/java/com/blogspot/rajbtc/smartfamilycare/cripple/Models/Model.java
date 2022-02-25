package com.blogspot.rajbtc.smartfamilycare.cripple.Models;


import com.google.firebase.firestore.DocumentReference;


public class Model {
    DocumentReference reference;
    String name;
    String bp;
    String bs;
    String num;
    String bg;


    public Model() {
    }

    public Model(DocumentReference reference, String name, String bp, String bs, String num, String bg) {
        this.reference = reference;
        this.name = name;
        this.bp = bp;
        this.bs = bs;
        this.num = num;
        this.bg = bg;
    }


    public DocumentReference getReference() {
        return reference;
    }

    public String getName() {
        return name;
    }

    public String getBp() {
        return bp;
    }

    public String getBs() {
        return bs;
    }

    public String getNum() {
        return num;
    }

    public String getBg() {
        return bg;
    }
}
