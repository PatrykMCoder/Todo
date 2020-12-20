package com.pmprogramms.todo.API;

import androidx.annotation.Nullable;

import com.pmprogramms.todo.API.jsonhelper.JSONHelperEditTodo;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperLastEdit;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperCustomTags;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperDataTodo;
import com.pmprogramms.todo.API.jsonhelper.note.JSONHelperNote;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperTag;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperTodo;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperSaveTodo;
import com.pmprogramms.todo.API.jsonhelper.user.JSONHelperUser;
import com.pmprogramms.todo.API.jsonhelper.user.JSONHelperUserID;
import com.pmprogramms.todo.API.reader.ReaderFromJSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pmprogramms.todo.utils.device.CheckTypeApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class APIClient {

    private final String mainURL = buildMainURL();

    private URL registerUrl = makeUrl("/user");
    private URL loginUrl = makeUrl("/login");
    private URL createTODOURL = makeUrl("/create_todo");
    private URL createNoteURL = makeUrl("/create_note");

    private String email, password, username;
    private HttpURLConnection connection;

    private String buildMainURL() {
        if (CheckTypeApplication.isDebugApp()) return "http://10.0.2.2:4000";
        return "https://todo-note-api.herokuapp.com";
    }

    private URL makeUrl(String endpoint) {
        try {
            return new URL(mainURL + endpoint);
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

            JSONHelperUserID userObjectID = new ReaderFromJSON(connection).getUserObjectID();
            userID = userObjectID.user_id;

            ArrayList<Object> data = new ArrayList<>();

            data.add(connection.getResponseCode());
            data.add(userID);
            return data;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public JSONHelperUser loadDataUser(String userID) {
        URL userURL = makeUrl("/user/" + userID);
        try {
            if (userURL != null) {
                createConnection(userURL, "GET");
                return new ReaderFromJSON(connection).readUserData();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new JSONHelperUser("", "");
    }

    public ArrayList<JSONHelperTodo> loadTitlesTodoUser(String userID) {
        try {
            URL todosURL = makeUrl("/todos/" + userID);
            if (todosURL != null) {
                createConnection(todosURL, "GET");
                return new ReaderFromJSON(connection).readTitlesTodo();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<JSONHelperDataTodo> loadTodos(String userID, String todoID) {
        try {
            URL todosURL = makeUrl("/todos/" + userID + "/" + todoID);
            if (todosURL != null) {
                createConnection(todosURL, "GET");
                return new ReaderFromJSON(connection).readTodos();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public JSONHelperLastEdit loadTodosLastEdit(String userID, String todoID) {
        try {
            URL todosURL = makeUrl("/todos/" + userID + "/" + todoID);
            if (todosURL != null) {
                createConnection(todosURL, "GET");
                return new ReaderFromJSON(connection).readTodosLastEdit();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return new JSONHelperLastEdit("");
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

    public JSONHelperTag getTagTodo(String userID, String todoID) {
        try {
            URL todosURL = makeUrl("/todos/" + userID + "/" + todoID);
            if (todosURL != null) {
                createConnection(todosURL, "GET");
                return new ReaderFromJSON(connection).readTag();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return new JSONHelperTag("");
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

    public ArrayList<JSONHelperCustomTags> loadUserTags(String userId) {
        try {
            URL loadCustomTagsUrl = makeUrl("/tags/" + userId);
            if (loadCustomTagsUrl != null) {
                createConnection(loadCustomTagsUrl, "GET");
                return new ReaderFromJSON(connection).getUserCustomTags(connection);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
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

    public ArrayList<JSONHelperNote> loadTitlesNoteUser(String userID) {
        try {
            URL notesURL = makeUrl("/notes/" + userID);

            if (notesURL != null) {
                createConnection(notesURL, "GET");
                return new ReaderFromJSON(connection).readTitlesNote();
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public int saveNote(String userID, String title, String contents) {
        try {
            if (createNoteURL != null) {
                createConnection(createNoteURL, "POST");

                JSONObject dataJson = new JSONObject();
                dataJson.put("title", title);
                dataJson.put("user_id", userID);
                dataJson.put("contents", contents);
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

    public ArrayList<JSONHelperNote> loadNotePreview(String userID, String noteID) {
        try {
            URL todosURL = makeUrl("/notes/" + userID + "/" + noteID);
            if (todosURL != null) {
                createConnection(todosURL, "GET");
                return new ReaderFromJSON(connection).readNote();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public int deleteNote(String userID, String noteID) {
        try {
            URL deleteURL = makeUrl("/notes/" + userID + "/" + noteID);
            if (deleteURL != null) {
                createConnection(deleteURL, "DELETE");
                return connection.getResponseCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int editNote(String userID, String noteID, String title, String contents) {
        try {
            URL editNoteURL = makeUrl("/notes/" + userID + "/" + noteID);
            if (editNoteURL != null) {
                createConnection(editNoteURL, "PUT");
                JSONObject dataJson = new JSONObject();
                dataJson.put("title", title);
                dataJson.put("contents", contents);

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

    public int archiveNodeAction(String userID, String noteID, boolean archive) {
        try {
            URL archiveActionURL = makeUrl("/notes/archive/" + userID + "/" + noteID);
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