// src/main/java/com/example/vietflightinventory/utils/SessionManager.java
package com.example.vietflightinventory.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.vietflightinventory.models.User;
import com.google.gson.Gson;

public class SessionManager {

    private static final String PREF_NAME = "VietFlightSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_DATA = "userData";

    private static SessionManager instance;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    private SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveUserSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_DATA, gson.toJson(user));
        editor.apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public User getCurrentUser() {
        if (!isLoggedIn()) {
            return null;
        }

        String userJson = preferences.getString(KEY_USER_DATA, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }

        return null;
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public void updateUserData(User user) {
        if (isLoggedIn()) {
            editor.putString(KEY_USER_DATA, gson.toJson(user));
            editor.apply();
        }
    }
}