package de.hu.java.vthreads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hu.java.vthreads.user.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.List;

public class GetUsers {

    public static void main(final String... args) throws Exception {
        final List<User> users = getUsers();
        System.out.println(users);
    }

    public static List<User> getUsers() throws IOException, InterruptedException {
        try (var client = HttpClient.newHttpClient()) {
            final var request = HttpRequest.newBuilder()
                    .uri(URI.create("https://jsonplaceholder.typicode.com/users"))
                    .timeout(Duration.ofMinutes(1))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            final HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            System.out.println(response.body());
            System.out.println(response.statusCode());
            return deserializeUsers(response.body());
        }
    }

    private static List<User> deserializeUsers(final String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, new TypeReference<List<User>>() {
        });
    }
}
