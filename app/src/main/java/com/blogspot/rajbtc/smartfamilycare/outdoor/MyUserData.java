package com.blogspot.rajbtc.smartfamilycare.outdoor;

public class MyUserData {
    private String name, email, accessPass;

    public MyUserData() {
    }

    public MyUserData(String name, String email, String accessPass) {
        this.name = name;
        this.email = email;
        this.accessPass = accessPass;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAccessPass() {
        return accessPass;
    }
}
