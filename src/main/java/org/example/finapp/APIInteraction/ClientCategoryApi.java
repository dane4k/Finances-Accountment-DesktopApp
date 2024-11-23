package org.example.finapp.APIInteraction;

import com.fasterxml.jackson.databind.ObjectMapper;


import okhttp3.*;
import org.example.finapp.models.CategoryDTO;

import java.io.IOException;
import java.util.List;

public class ClientCategoryApi {

    private static final String BASE_URL = "http://localhost:8080/api/categories";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * реквест на получение категорий
     * @param username имя юзера
     * @return категории юзера и общие
     * @throws IOException
     */
    public List<CategoryDTO> getCategories(String username) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/" + username)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return objectMapper.readValue(response.body().string(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CategoryDTO.class));
        }
    }

    /**
     * реквест на добавление категории
     * @param username имя пользователя
     * @param categoryDTO dto для передачи
     * @return результат реквеста
     * @throws IOException
     */
    public String addCategory(String username, CategoryDTO categoryDTO) throws IOException {
        String url = BASE_URL + "/" + username;

        String json = objectMapper.writeValueAsString(categoryDTO);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return "Категория добавлена";
            } else {
                return response.body().string();
            }
        }
    }


    /**
     * реквест на удаление категории
     * @param username ник
     * @param categoryName название категории
     * @return результат запроса
     * @throws IOException
     */
    public String deleteCategory(String username, String categoryName) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/" + username + "/" + categoryName)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return "Категория удалена";
            } else {
                return response.body().string();
            }
        }
    }
}