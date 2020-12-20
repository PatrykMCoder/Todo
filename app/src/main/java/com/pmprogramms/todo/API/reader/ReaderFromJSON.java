package com.pmprogramms.todo.API.reader;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperLastEdit;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperCustomTags;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperDataTodo;
import com.pmprogramms.todo.API.jsonhelper.note.JSONHelperNote;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperTag;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperTodo;
import com.pmprogramms.todo.API.jsonhelper.user.JSONHelperUser;
import com.pmprogramms.todo.API.jsonhelper.user.JSONHelperUserID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ReaderFromJSON {
    private final HttpURLConnection connection;

    public ReaderFromJSON(HttpURLConnection connection) {
        this.connection = connection;
    }

    public ArrayList<JSONHelperTodo> readTitlesTodo() throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        line = builder.toString();

        JSONObject jsonObject = new JSONObject(line);
        JSONArray dataObject = jsonObject.getJSONArray("data");
        return new Gson().fromJson(dataObject.toString(), new TypeToken<ArrayList<JSONHelperTodo>>() {
        }.getType());
    }

    public ArrayList<JSONHelperDataTodo> readTodos() throws IOException, JSONException {
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

        return new Gson().fromJson(dataTodoArray.toString(), new TypeToken<ArrayList<JSONHelperDataTodo>>() {
        }.getType());
    }

    public JSONHelperUser readUserData() throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        line = builder.toString();

        JSONObject jsonObject = new JSONObject(line);
        JSONObject userObject = jsonObject.getJSONObject("data");
        return new Gson().fromJson(userObject.toString(), new TypeToken<JSONHelperUser>() {
        }.getType());
    }

    public JSONHelperTag readTag() throws IOException, JSONException {
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
        return new Gson().fromJson(dataTodoObject.toString(), JSONHelperTag.class);
    }

    public JSONHelperLastEdit readTodosLastEdit() throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        line = builder.toString();

        JSONObject jsonObject = new JSONObject(line);
        JSONArray dataObject = jsonObject.getJSONArray("data");
        JSONObject dataObjeTodo = dataObject.getJSONObject(0);

        return new Gson().fromJson(dataObjeTodo.toString(), JSONHelperLastEdit.class);
    }

    public JSONHelperUserID getUserObjectID() throws IOException, JSONException {
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
        return new Gson().fromJson(dataObject.toString(), JSONHelperUserID.class);
    }

    public ArrayList<JSONHelperCustomTags> getUserCustomTags(HttpURLConnection connection) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        line = builder.toString();

        JSONObject jsonObject = new JSONObject(line);
        return new Gson().fromJson(jsonObject.get("data").toString(), new TypeToken<ArrayList<JSONHelperCustomTags>>(){}.getType());
    }

    public ArrayList<JSONHelperNote> readTitlesNote() throws JSONException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        line = builder.toString();

        JSONObject jsonObject = new JSONObject(line);
        JSONArray dataObject = jsonObject.getJSONArray("data");
        return new Gson().fromJson(dataObject.toString(), new TypeToken<ArrayList<JSONHelperNote>>() {
        }.getType());
    }

    public ArrayList<JSONHelperNote> readNote() throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        line = builder.toString();

        JSONObject jsonObject = new JSONObject(line);
        JSONArray dataObject = jsonObject.getJSONArray("data");
        Log.d("ABCDEFGH", "readNote: " + dataObject);
        Log.d("ABCDEFGH", "readNote: " + jsonObject);
        return new Gson().fromJson(dataObject.toString(), new TypeToken<ArrayList<JSONHelperNote>>() {
        }.getType());
    }
}
