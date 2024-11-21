package org.example.finapp.APIInteraction;

import okhttp3.*;

import java.io.IOException;

import org.json.JSONObject;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api/users";
    private final OkHttpClient client = new OkHttpClient();

    public String addUser(String username, String password) throws IOException {

        RequestBody body = createRequestBody(username, password);

        Request request = new Request.Builder()
                .url(BASE_URL + "/register")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return "Пользователь зарегистрирован";
            } else {
                return response.body().string();
            }
        }
        catch (IOException e) {
            return e.getMessage();
        }
    }

    public String logInUser(String username, String password) throws IOException {

        RequestBody body = createRequestBody(username, password);

        Request request = new Request.Builder()
                .url(BASE_URL + "/login")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return "Успешный вход";
            } else {
                return response.body().string();
            }
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    private RequestBody createRequestBody(String username, String password) {
        JSONObject json = new JSONObject();

        json.put("username", username);
        json.put("password", password);

        String jsonString = json.toString();

        return RequestBody.create(MediaType.
                parse("application/json; charset=utf-8"), jsonString);
    }
}