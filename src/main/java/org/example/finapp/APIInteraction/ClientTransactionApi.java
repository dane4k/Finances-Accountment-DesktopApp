package org.example.finapp.APIInteraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.*;
import org.example.finapp.models.TransactionDTO;

import java.io.IOException;
import java.util.List;

public class ClientTransactionApi {

    private static final String BASE_URL = "http://localhost:8080/api/transactions";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper;

    public ClientTransactionApi() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * реквест на получение транз
     * @param username ник
     * @return список транз юзера
     * @throws IOException
     */
    public List<TransactionDTO> getTransactions(String username) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/" + username)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return objectMapper.readValue(response.body().string(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, TransactionDTO.class));
        }
    }


    /**
     * реквест на добавление транзакции
     * @param username юзернейм
     * @param transactionDTO dto для передачи
     * @return результат запроса
     * @throws IOException
     */
    public String addTransaction(String username, TransactionDTO transactionDTO) throws IOException {
        String url = BASE_URL + "/" + username;

        String json = objectMapper.writeValueAsString(transactionDTO);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return "Транзакция добавлена";
            } else {
                return response.body().string();
            }
        }
    }


    /**
     * реквест на удаление транзакции
     * @param username ник
     * @param transactionId айди транзы
     * @return результат реквеста
     * @throws IOException
     */
    public String deleteTransaction(String username, Long transactionId) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/" + username + "/" + transactionId)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return "Транзакция удалена";
            } else {
                return response.body().string();
            }
        }
    }
}