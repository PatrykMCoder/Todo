package com.pmprogramms.todo.API.retrofit;

import com.pmprogramms.todo.API.jsonhelper.JSONHelperCustomTags;
import com.pmprogramms.todo.API.jsonhelper.user.JSONHelperUser;
import com.pmprogramms.todo.API.retrofit.customTags.JsonHelperTag;
import com.pmprogramms.todo.API.retrofit.login.JsonHelperLogin;
import com.pmprogramms.todo.API.retrofit.todo.Data;
import com.pmprogramms.todo.API.retrofit.todo.JSONHelperTodo;
import com.pmprogramms.todo.API.retrofit.user.JsonHelperUser;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface API {
    @POST("/login")
    Call<JsonHelperLogin> loginUser(@Body HashMap<String, String> data);

    @POST("/user")
    Call<Void> createUser(@Body HashMap<String, Object> data);

    @GET("/todos/{userId}")
    Call<JSONHelperTodo> getUserTodosTitle(@Path("userId") String id);

    @GET("/todos/{userId}/{todoId}")
    Call<JSONHelperTodo> getUserTodoData(@Path("userId") String userId, @Path("todoId") String todoId);

    @PUT("/todos/status/{userId}/{todoId}")
    Call<Data> updateTodoStatus(@Path("userId") String userId, @Path("todoId") String todoId, @Body HashMap<String, String> todos);

    @PUT("/todos/archive/{userId}/{todoId}")
    Call<Void> archiveAction(@Path("userId") String userId, @Path("todoId") String todoId, @Body HashMap<String, Boolean> archiveStatus);

    @PUT("/todos/{userId}/{todoId}")
    Call<Void> editTodo(@Path("userId") String userId, @Path("todoId") String todoId, @Body HashMap<String, String> data);

    @POST("/create_todo")
    Call<Void> saveTodo(@Body HashMap<String, String> data);

    @GET("/tags/{userId}")
    Call<JsonHelperTag> getUserTagData(@Path("userId") String userId);

    @GET("/user/{userId}")
    Call<JsonHelperUser> getUserData(@Path("userId") String userId);

    @DELETE("/todos/{userId}/{todoId}")
    Call<Void> deleteTodo(@Path("userId") String userId, @Path("todoId") String todoId);

    @PUT("/user/edit/{userId}")
    Call<JSONHelperUser> editProfile(@Path("userId") String userId, @Body HashMap<String, String> data);

    @POST("/create_tag")
    Call<Void> createCustomTag(@Body HashMap<String, String> data);

    @GET("/tags/{userId}")
    Call<JsonHelperTag> loadCustomTags(@Path("userId") String userID);

    @DELETE("/tags/{userId}/{tagId}")
    Call<Void> deleteCustomTag(@Path("userId") String userID, @Path("tagId") String tagId);
}
