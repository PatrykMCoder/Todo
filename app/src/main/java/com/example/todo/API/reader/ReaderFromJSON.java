package com.example.todo.API.reader;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class ReaderFromJSON {
    private HttpURLConnection connection;

    public ReaderFromJSON(HttpURLConnection connection) {
        this.connection = connection;
    }

    public String readTitlesTodo() throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        line = builder.toString();

        JSONObject jsonObject = new JSONObject(line);
        JSONArray dataObject = jsonObject.getJSONArray("data");
        return dataObject.toString();
    }

    public String readTodos() throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        line = builder.toString();

        JSONObject jsonObject = new JSONObject(line);
        JSONArray dataObject = jsonObject.getJSONArray("data");
        JSONObject dataTodoObject = dataObject.getJSONObject(0);
        JSONArray dataTodoArray = dataTodoObject.getJSONArray("todos");

        return dataTodoArray.toString();
    }

    public String readUserData() throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        line = builder.toString();

        JSONObject jsonObject = new JSONObject(line);
        JSONObject userObject = jsonObject.getJSONObject("data");
        return userObject.toString();
    }

    public String readTag() throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        line = builder.toString();

        JSONObject jsonObject = new JSONObject(line);
        JSONArray dataObject = jsonObject.getJSONArray("data");
        JSONObject dataTodoObject = dataObject.getJSONObject(0);

        return dataTodoObject.getString("tag");
    }

    public String readTodosLastEdit() throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        line = builder.toString();

        JSONObject jsonObject = new JSONObject(line);
        JSONArray dataObject = jsonObject.getJSONArray("data");
        JSONObject dataObject2 = dataObject.getJSONObject(0);

        return dataObject2.getString("updatedAt");
    }

    public String getUserObjectID() throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        line = builder.toString();

        JSONObject jsonObject = null;
        jsonObject = new JSONObject(line);
        JSONObject dataObject = jsonObject.getJSONObject("data");
        return dataObject.getString("user_id");
    }
}
