package com.pmprogramms.todo.API.retrofit;

import com.pmprogramms.todo.API.retrofit.customTags.JsonHelperTag;
import com.pmprogramms.todo.API.retrofit.login.JsonHelperLogin;
import com.pmprogramms.todo.API.retrofit.todo.todo.Data;
import com.pmprogramms.todo.API.retrofit.todo.todo.JSONHelperTodo;
import com.pmprogramms.todo.API.retrofit.user.JsonHelperUser;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface API {
    @POST("/login")
    Call<JsonHelperLogin> loginUser(@Body HashMap<String, String> data);

    @POST("/user")
    Call<Void> createUser(@Body HashMap<String, Object> data);

    @GET("/todos")
    Call<JSONHelperTodo> getUserTodosTitle(@Header("x-access-token") String token);

    @GET("/todos/{todoId}")
    Call<JSONHelperTodo> getUserTodoData(@Path("todoId") String todoId, @Header("x-access-token") String token);

    @PUT("/todos/status/{todoId}")
    Call<Data> updateTodoStatus(@Path("todoId") String todoId, @Body HashMap<String, String> todos, @Header("x-access-token") String token);

    @PUT("/todos/archive/{todoId}")
    Call<Void> archiveAction(@Path("todoId") String todoId, @Body HashMap<String, Boolean> archiveStatus, @Header("x-access-token") String token);

    @PUT("/todos/{todoId}")
    Call<Void> editTodo(@Path("todoId") String todoId, @Body HashMap<String, String> data, @Header("x-access-token") String token);

    @POST("/create_todo")
    Call<Void> saveTodo(@Body HashMap<String, String> data, @Header("x-access-token") String token);

    @GET("/tags")
    Call<JsonHelperTag> getUserTagData(@Header("x-access-token") String token);

    @GET("/user")
    Call<JsonHelperUser> getUserData(@Header("x-access-token") String token);

    @DELETE("/todos/{todoId}")
    Call<Void> deleteTodo(@Path("todoId") String todoId, @Header("x-access-token") String token);

    //todo - make new system for edit. we can't now use login method
    @PUT("/user/edit")
    Call<Void> editProfile(@Body HashMap<String, String> data, @Header("x-access-token") String token);

    @POST("/create_tag")
    Call<Void> createCustomTag(@Body HashMap<String, String> data, @Header("x-access-token") String token);

    @GET("/tags")
    Call<JsonHelperTag> loadCustomTags(@Header("x-access-token") String token);

    @DELETE("/tags/{tagId}")
    Call<Void> deleteCustomTag(@Path("tagId") String tagId, @Header("x-access-token") String token);
}
