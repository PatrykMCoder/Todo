package com.pmprogramms.todo.API;

import android.util.Log;

import androidx.annotation.Nullable;

import com.pmprogramms.todo.API.jsonhelper.JSONHelperEditTodo;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperSaveTodo;
import com.pmprogramms.todo.API.reader.ReaderFromJSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class APIClient {

    private final String mainURL = "https://todo-note-api.herokuapp.com";

    private URL registerUrl = makeUrl("/user");
    private URL loginUrl = makeUrl("/login");
    private URL createTODOURL = makeUrl("/create_todo");

    private String email, password, username;
    private HttpURLConnection connection;

    private URL makeUrl(String endpoint) {
        try { return new URL(mainURL + endpoint);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public APIClient() {
    }

    public APIClient(String username, String email, String password) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public APIClient(String email, String password) {
        this.email = email;
        this.password = password;
    }

    private void createConnection(URL url, String method) throws IOException {
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        connection.setRequestProperty("Accept", "application/json");
    }

    public int createUser(boolean accept) {
        try {
            createConnection(registerUrl, "POST");
            JSONObject dataJson = new JSONObject();
            dataJson.put("username", username);
            dataJson.put("email", email);
            dataJson.put("password", password);
            dataJson.put("accept_privacy", accept);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(dataJson.toString().getBytes(StandardCharsets.UTF_8));
            dataOutputStream.flush();
            dataOutputStream.close();
            return connection.getResponseCode();
        } catch (IOException | JSONException e) { 
            e.printStackTrace();
        }
        return 0;
    }

    public ArrayList<Object> loginUser() {
        String userID = "";
        try {
            createConnection(loginUrl, "POST");

            JSONObject dataJson = new JSONObject();
            dataJson.put("email", email);
            dataJson.put("password", password);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(dataJson.toString().getBytes(StandardCharsets.UTF_8));
            dataOutputStream.flush();
            dataOutputStream.close();

            userID = new ReaderFromJSON(connection).getUserObjectID();

            ArrayList<Object> data = new ArrayList<>();

            data.add(connection.getResponseCode());
            data.add(userID);
            return data;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String loadDataUser(String userID) {
        URL userURL = makeUrl("/user/" + userID);
        try {
            if (userURL != null) {
                createConnection(userURL, "GET");
                return new ReaderFromJSON(connection).readUserData();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String loadTitlesTodoUser(String userID) {
        try {
            URL todosURL = makeUrl("/todos/" + userID);
            if (todosURL != null) {
                createConnection(todosURL, "GET");
                return new ReaderFromJSON(connection).readTitlesTodo();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String loadTodos(String userID, String todoID) {
        try {
            URL todosURL = makeUrl("/todos/" + userID + "/" + todoID);
            if (todosURL != null) {
                createConnection(todosURL, "GET");
                return new ReaderFromJSON(connection).readTodos();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String loadTodosLastEdit(String userID, String todoID) {
        try {
            URL todosURL = makeUrl("/todos/" + userID + "/" + todoID);
            if (todosURL != null) {
                createConnection(todosURL, "GET");
                return new ReaderFromJSON(connection).readTodosLastEdit();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int editUserProfile(String userID, String username, String emailOld, @Nullable String emailNew, @Nullable String password) {
        try {
            URL userEditURL = makeUrl("/user/edit/" + userID);
            if (userEditURL != null) {
                createConnection(userEditURL, "PUT");
                JSONObject dataToUpdate = new JSONObject();

                if (emailNew != null) dataToUpdate.put("email_new", emailNew);
                dataToUpdate.put("email_old", emailOld);
                dataToUpdate.put("username", username);
                if (password != null) dataToUpdate.put("password", password);

                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(dataToUpdate.toString().getBytes(StandardCharsets.UTF_8));
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
            if (createTODOURL != null) {
                createConnection(createTODOURL, "POST");

                JSONObject dataJson = new JSONObject();
                dataJson.put("title", title);
                dataJson.put("user_id", userID);
                dataJson.put("todos", new Gson().toJson(todos, new TypeToken<ArrayList<JSONHelperSaveTodo>>() {
                }.getType()));
                dataJson.put("tag", tag);
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(dataJson.toString().getBytes(StandardCharsets.UTF_8));
                dataOutputStream.flush();
                dataOutputStream.close();

                return connection.getResponseCode();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int deleteTodo(String userID, String todoID) {
        try {
            URL deleteURL = makeUrl("/todos/" + userID + "/" + todoID);
            if (deleteURL != null) {
                createConnection(deleteURL, "DELETE");
                return connection.getResponseCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int editTodo(String userID, String todoID, String title, ArrayList<JSONHelperEditTodo> todos, String tag) {
        try {
            URL editTodoURL = makeUrl("/todos/" + userID + "/" + todoID);
            if (editTodoURL != null) {
                createConnection(editTodoURL, "PUT");
                JSONObject dataJson = new JSONObject();
                dataJson.put("title", title);
                dataJson.put("todos", new Gson().toJson(todos, new TypeToken<ArrayList<JSONHelperSaveTodo>>() {
                }.getType()));
                dataJson.put("tag", tag);

                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(dataJson.toString().getBytes(StandardCharsets.UTF_8));
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
            URL todosURL = makeUrl("/todos/" + userID + "/" + todoID);
            if (todosURL != null) {
                createConnection(todosURL, "GET");
                return new ReaderFromJSON(connection).readTag();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int editTodoTaskStatus(String userID, String todoID, ArrayList<JSONHelperEditTodo> todos) {
        try {
            URL editTodoURL = makeUrl("/todos/status/" + userID + "/" + todoID);
            if (editTodoURL != null) {
                createConnection(editTodoURL, "PUT");

                JSONObject dataJson = new JSONObject();
                dataJson.put("todos", new Gson().toJson(todos, new TypeToken<ArrayList<JSONHelperSaveTodo>>() {
                }.getType()));

                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(dataJson.toString().getBytes(StandardCharsets.UTF_8));
                dataOutputStream.flush();
                dataOutputStream.close();

                return connection.getResponseCode();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String loadUserTags(String userId) {
        try {
            URL loadCustomTagsUrl = makeUrl("/tags/" + userId);
            if (loadCustomTagsUrl != null) {
                createConnection(loadCustomTagsUrl, "GET");
                return new ReaderFromJSON(connection).getUserCustomTags(connection);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int createUserCustomTag(String userID, String nameTag) {
        try {
            URL createCustomTagURL = makeUrl("/create_tag");
            if (createCustomTagURL != null) {
                createConnection(createCustomTagURL, "POST");

                JSONObject dataJson = new JSONObject();
                dataJson.put("tag_name", nameTag);
                dataJson.put("user_id", userID);
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(dataJson.toString().getBytes(StandardCharsets.UTF_8));
                dataOutputStream.flush();
                dataOutputStream.close();

                return connection.getResponseCode();
            }
            return 0;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int deleteCustomTag(String userID, String tagID) {
        try {
            URL deleteTagURL = makeUrl("/tags/" + userID + "/" + tagID);
            if (deleteTagURL != null) {
                createConnection(deleteTagURL, "DELETE");
                return connection.getResponseCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int archiveTodoAction(String userID, String todoID, boolean archive) {
        try {
            URL archiveActionURL = makeUrl("/todos/archive/" + userID + "/" + todoID);
            if (archiveActionURL != null) {
                createConnection(archiveActionURL, "PUT");

                JSONObject dataJson = new JSONObject();
                dataJson.put("archive", archive);

                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(dataJson.toString().getBytes(StandardCharsets.UTF_8));
                dataOutputStream.flush();
                dataOutputStream.close();

                return connection.getResponseCode();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
