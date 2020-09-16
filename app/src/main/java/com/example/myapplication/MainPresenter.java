package com.example.myapplication;


public final class MainPresenter {

    private static MainPresenter instance = null;

    private static final Object syncObj = new Object();

    private String counter;
    private String uri1;
    private String uri2;
    private String uri3;
    private String name1;
    private String name2;
    private String name3;
    private String token;
    private String email;




    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUri2() {
        return uri2;
    }

    public void setUri2(String uri2) {
        this.uri2 = uri2;
    }

    public String getUri3() {
        return uri3;
    }

    public void setUri3(String uri3) {
        this.uri3 = uri3;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }



    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }




    public String getUri1() {
        return uri1;
    }

    public void setUri1(String URI1) {
        this.uri1 = URI1;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private MainPresenter() {
        counter = "1";
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public static MainPresenter getInstance() {
        synchronized (syncObj) {
            if (instance == null) {
                instance = new MainPresenter();
            }
            return instance;
        }
    }
}