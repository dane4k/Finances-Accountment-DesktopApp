package org.example.finapp.APIInteraction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

public class ClientDiagramApi {

    private static final String BASE_URL = "http://localhost:8080/home/transactions";
    private final OkHttpClient client = new OkHttpClient();


    /**
     * реквест на получение данных для диаграмм
     * @param username ник
     * @return мапа с транзами юзера за последние 30 дней
     * @throws IOException
     */
    public Map<String, Map<String, Double>> getTransactionStats(String username) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/stats?username=" + username)
                .get()
                .build();

        String jsonResponse = executeRequest(request);
        return parseJsonResponse(jsonResponse);
    }

    /**
     * выполнение реквеста
     * @param request
     * @return
     * @throws IOException
     */
    private String executeRequest(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                return responseBody;
            } else {
                throw new IOException("Ошибка: " + response.code() + " " + response.message());
            }
        }
    }

    /**
     * парс жсона
     * @param jsonResponse
     * @return
     */
    private Map<String, Map<String, Double>> parseJsonResponse(String jsonResponse) {
        Gson gson = new Gson();
        return gson.fromJson(jsonResponse, new TypeToken<Map<String, Map<String, Double>>>(){}.getType());
    }
}