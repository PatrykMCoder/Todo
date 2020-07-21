package com.example.todo.service;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MongoDBClient {
    private static String TAG = "mongodbclient";

    private URL registerUrl = makeUrl("https://todo-note-api.herokuapp.com/user");
    private URL loginUrl = makeUrl("https://todo-note-api.herokuapp.com/login");

    private String email, password, username;

    private JSONObject jsonObject;

    private URL makeUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MongoDBClient(String username, String email, String password) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public MongoDBClient(String email, String password) {
        this.email = email;
        this.password = password;
    }

    private JSONObject createJsonObject(HttpURLConnection connection) throws IOException, JSONException {
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line = "";

        while ((line = streamReader.readLine()) != null){
            response.append(line);
        }
        return new JSONObject(response.toString());
    }

    public int createUser() {
        try {
            HttpURLConnection connection = (HttpURLConnection) registerUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            JSONObject dataJson = new JSONObject();
            dataJson.put("username", username);
            dataJson.put("email", email);
            dataJson.put("password", password);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(dataJson.toString());
            dataOutputStream.flush();
            dataOutputStream.close();
           return connection.getResponseCode();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "createUser: " + e);
        }
        return 0;
    }

    public JSONObject getUserObject() {
        return null;
    }

    public int loginUser() {
        try {
            HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            JSONObject dataJson = new JSONObject();
            dataJson.put("email", email);
            dataJson.put("password", password);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(dataJson.toString());
            dataOutputStream.flush();
            dataOutputStream.close();

            Log.d(TAG, "loginUser: " + connection.getResponseMessage() + "" + connection.getResponseCode());

            return connection.getResponseCode();
        } catch (IOException | JSONException e) {
           e.printStackTrace();
            Log.d(TAG, "loginUser: " + e);
        }
        return 0;
    }
}
