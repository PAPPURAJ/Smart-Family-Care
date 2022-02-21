package com.blogspot.rajbtc.smartfamilycare.outdoor;

import com.google.firebase.firestore.DocumentReference;

public class MemberlistData {

    DocumentReference reference;
    String email;

    public MemberlistData() {
    }

    public MemberlistData(DocumentReference reference, String email) {
        this.reference = reference;
        this.email = email;
    }

    public DocumentReference getReference() {
        return reference;
    }

    public String getEmail() {
        return email;
    }
}
