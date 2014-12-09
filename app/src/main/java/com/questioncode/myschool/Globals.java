package com.questioncode.myschool;

import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by alain on 05/12/14.
 */
public class Globals {
    private String accessToken;
    //public static String AUTH_URL = "http://api.androidhive.info/volley/person_object.json";
/*    public static String AUTH_URL = "http://10.0.2.2:9000/auth/local";
    public static String PROTECTED_URL ="http://10.0.2.2:9000/api/users/me";*/
    public static String AUTH_URL = "http://questioncode.fr/login";
    public static String PROTECTED_URL ="http://questioncode.fr/admin";

    private static Globals ourInstance = new Globals();

    public static Globals getInstance() {
        return ourInstance;
    }

    private Globals() {
    }


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
