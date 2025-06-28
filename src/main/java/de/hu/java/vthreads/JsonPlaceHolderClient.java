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
import java.util.logging.Logger;

public class JsonPlaceHolderClient {
    private static final URI BASE_URL = URI.create("https://jsonplaceholder.typicode.com");
    private static final Logger logger = Logger.getLogger(JsonPlaceHolderClient.class.getName());

    public static void main(final String... args) throws Exception {
        final List<User> users = new JsonPlaceHolderClient().getUsers();
        System.out.println(users);
    }

    public List<User> getUsers() throws IOException, InterruptedException {
        try (var client = newHttp2Client()) {
            final var request = HttpRequest.newBuilder()
                    .uri(BASE_URL.resolve("/users"))
                    .timeout(Duration.ofMinutes(1))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            final HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            logger.fine(() -> "Response: " + response.body());
            logger.fine(() -> "Status code: " + response.statusCode());
            return deserializeUsers(response.body());
        }
    }

    private static HttpClient newHttp2Client() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    private static List<User> deserializeUsers(final String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, new TypeReference<List<User>>() {
        });
    }
}
