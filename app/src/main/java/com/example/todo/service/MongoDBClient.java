package com.example.todo.service;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.todo.service.jsonhelper.JSONHelperEditTodo;
import com.example.todo.service.jsonhelper.JSONHelperSaveTodo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class MongoDBClient {
    private static String TAG = "mongodbclient";

    private URL registerUrl = makeUrl("https://todo-note-api.herokuapp.com/user");
    private URL loginUrl = makeUrl("https://todo-note-api.herokuapp.com/login");
    private URL createTODOURL = makeUrl("https://todo-note-api.herokuapp.com/create_todo");

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

    public MongoDBClient() {
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

        while ((line = streamReader.readLine()) != null) {
            response.append(line);
        }
        return new JSONObject(response.toString());
    }

    public int createUser(boolean accept) {
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
            dataJson.put("accept_privacy", accept);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(dataJson.toString());
            dataOutputStream.flush();
            dataOutputStream.close();
            Log.d(TAG, "createUser: " + dataJson);
            return connection.getResponseCode();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "createUser: " + e);
        }
        return 0;
    }

    public ArrayList<Object> loginUser() {
        String userID = "";
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

            userID = getUserObjectID(connection);

            ArrayList<Object> data = new ArrayList<>();

            data.add(connection.getResponseCode());
            data.add(userID);
            return data;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "loginUser: " + e);
        }
        return null;
    }

    public String loadDataUser(String userID) {
        URL userURL = makeUrl("https://todo-note-api.herokuapp.com/user/" + userID);
        try {
            HttpURLConnection connection = (HttpURLConnection) userURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");

            return readUserData(connection);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "loadDataUser: " + e.getMessage());
        }
        return null;
    }

    public String loadTitlesTodoUser(String userID) {
        try {
            URL todosURL = makeUrl("https://todo-note-api.herokuapp.com/todos/" + userID);
            if (todosURL != null) {
                HttpURLConnection connection = (HttpURLConnection) todosURL.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");

                return readTitlesTodo(connection);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String loadTodos(String userID, String todoID) {
        try {
            URL todosURL = makeUrl("https://todo-note-api.herokuapp.com/todos/" + userID + "/" + todoID);
            if (todosURL != null) {
                HttpURLConnection connection = (HttpURLConnection) todosURL.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");

                return readTodos(connection);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String loadTodosLastEdit(String userID, String todoID) {
        try {
            URL todosURL = makeUrl("https://todo-note-api.herokuapp.com/todos/" + userID + "/" + todoID);
            if (todosURL != null) {
                HttpURLConnection connection = (HttpURLConnection) todosURL.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");

                return readTodosLastEdit(connection);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int editUserProfile(String userID, String username, String emailOld, @Nullable String emailNew, @Nullable String password) {
        try {
            URL userEditURL = makeUrl("https://todo-note-api.herokuapp.com/user/edit/" + userID);
            if (userEditURL != null) {
                HttpURLConnection connection = (HttpURLConnection) userEditURL.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                JSONObject dataToUpdate = new JSONObject();
                if(emailNew != null) dataToUpdate.put("email_new", emailNew);
                dataToUpdate.put("email_old", emailOld);
                dataToUpdate.put("username", username);
                if (password != null) dataToUpdate.put("password", password);
                Log.d(TAG, "editUserProfile: " + dataToUpdate);
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes(dataToUpdate.toString());
                dataOutputStream.flush();
                dataOutputStream.close();
                Log.d(TAG, "editUserProfile: " + connection.getResponseMessage());
                return connection.getResponseCode();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String getUserObjectID(HttpURLConnection connection) throws IOException, JSONException {
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

    public int createNewTodo(String userID, String title, ArrayList<JSONHelperSaveTodo> todos, String tag) {
        try {
            HttpURLConnection connection = (HttpURLConnection) createTODOURL.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);


            JSONObject dataJson = new JSONObject();
            dataJson.put("title", title);
            dataJson.put("user_id", userID);
            dataJson.put("todos", new Gson().toJson(todos, new TypeToken<ArrayList<JSONHelperSaveTodo>>() {
            }.getType()));
            dataJson.put("tag", tag);
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(dataJson.toString());
            dataOutputStream.flush();
            dataOutputStream.close();

            return connection.getResponseCode();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int deleteTodo(String userID, String todoID) {
        try {
            URL deleteURL = makeUrl("https://todo-note-api.herokuapp.com/todos/" + userID + "/" + todoID);
            HttpURLConnection connection;
            if (deleteURL != null) {
                connection = (HttpURLConnection) deleteURL.openConnection();
                connection.setRequestMethod("DELETE");
                Log.d(TAG, "deleteTodo: " + userID);
                Log.d(TAG, "deleteTodo: " + todoID);
                Log.d(TAG, "deleteTodo: " + connection.getResponseCode());
                return connection.getResponseCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int editTodo(String userID, String todoID, ArrayList<JSONHelperEditTodo> todos, String tag) {
        try {
            URL editTodoURL = makeUrl("https://todo-note-api.herokuapp.com/todos/" + userID + "/" + todoID);
            HttpURLConnection connection;
            if (editTodoURL != null) {
                connection = (HttpURLConnection) editTodoURL.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                JSONObject dataJson = new JSONObject();
                dataJson.put("todos", new Gson().toJson(todos, new TypeToken<ArrayList<JSONHelperSaveTodo>>() {
                }.getType()));
                dataJson.put("tag", tag);

                Log.d(TAG, "doInBackground: " + dataJson);

                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes(dataJson.toString());
                dataOutputStream.flush();
                dataOutputStream.close();

                Log.d(TAG, "editTodo: " + connection.getResponseCode());
                Log.d(TAG, "editTodo: " + connection.getResponseMessage());

                return connection.getResponseCode();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getTagTodo(String userID, String todoID) {
        try {
            URL todosURL = makeUrl("https://todo-note-api.herokuapp.com/todos/" + userID + "/" + todoID);
            if (todosURL != null) {
                HttpURLConnection connection = (HttpURLConnection) todosURL.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");

                return readTag(connection);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String readTitlesTodo(HttpURLConnection connection) throws IOException, JSONException {
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

    private String readTodos(HttpURLConnection connection) throws IOException, JSONException {
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

    private String readUserData(HttpURLConnection connection) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        line = builder.toString();

        JSONObject jsonObject = new JSONObject(line);
        JSONObject userObject = jsonObject.getJSONObject("data");
        Log.d(TAG, "readUserData: " + userObject);
        return userObject.toString();
    }

    private String readTag(HttpURLConnection connection) throws IOException, JSONException {
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

    private String readTodosLastEdit(HttpURLConnection connection) throws IOException, JSONException {
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

    public int editTodoTaskStatus(String userID, String todoID, ArrayList<JSONHelperEditTodo> todos) {
        try {
            URL editTodoURL = makeUrl("https://todo-note-api.herokuapp.com/todos/status/" + userID + "/" + todoID);
            HttpURLConnection connection;
            if (editTodoURL != null) {
                connection = (HttpURLConnection) editTodoURL.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                JSONObject dataJson = new JSONObject();
                dataJson.put("todos", new Gson().toJson(todos, new TypeToken<ArrayList<JSONHelperSaveTodo>>() {
                }.getType()));

                Log.d(TAG, "doInBackground: " + dataJson);

                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes(dataJson.toString());
                dataOutputStream.flush();
                dataOutputStream.close();

                Log.d(TAG, "editTodo: " + connection.getResponseCode());
                Log.d(TAG, "editTodo: " + connection.getResponseMessage());

                return connection.getResponseCode();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
