package com.example.todo.API;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import com.example.todo.API.jsonhelper.JSONHelperEditTodo;
import com.example.todo.API.jsonhelper.JSONHelperSaveTodo;
import com.example.todo.API.reader.ReaderFromJSON;
import com.google.gson.Gson;
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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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

            userID = new ReaderFromJSON(connection).getUserObjectID();

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

            return new ReaderFromJSON(connection).readUserData();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
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

                return new ReaderFromJSON(connection).readTitlesTodo();
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

                return new ReaderFromJSON(connection).readTodos();
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

                return new ReaderFromJSON(connection).readTodosLastEdit();
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

                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes(dataToUpdate.toString());
                dataOutputStream.flush();
                dataOutputStream.close();
                return connection.getResponseCode();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return 0;
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

                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes(dataJson.toString());
                dataOutputStream.flush();
                dataOutputStream.close();

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

                return new ReaderFromJSON(connection).readTag();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
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
