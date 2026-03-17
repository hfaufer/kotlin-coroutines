package de.hu.java.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hu.java.http.user.User;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class JsonPlaceHolderClient {
    private static final URI BASE_URL = URI.create("https://jsonplaceholder.typicode.com");
    private static final Logger logger = Logger.getLogger(JsonPlaceHolderClient.class.getName());

    static void main() throws Exception {
        IO.println("====== Synchronous GET request ======");
        final List<User> syncUsers= new JsonPlaceHolderClient().getUsers();
        IO.println("Sync users: " + syncUsers);

        IO.println("====== Asynchronous GET request ======");
        getUsersAsync()
                .thenAccept(users -> IO.println("Async users: " + users))
                .join();
    }

    public List<User> getUsers() throws IOException, InterruptedException {
        try (var client = newHttp2Client()) {
            final var request = getUsersRequest();
            final HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            logger.fine(() -> "Response: " + response.body());
            logger.fine(() -> "Status code: " + response.statusCode());
            return deserializeUsers(response.body());
        }
    }

    public static CompletableFuture<List<User>> getUsersAsync() {
        try (var client = newHttp2Client()) {
            return client.sendAsync(getUsersRequest(), BodyHandlers.ofString())
                    .thenApply(response -> deserializeUsers(response.body()));
        }
    }

    private static HttpClient newHttp2Client() {
        return HttpClient.newBuilder()
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    private static HttpRequest getUsersRequest() {
        return HttpRequest.newBuilder()
                .uri(BASE_URL.resolve("/users"))
                .timeout(Duration.ofMinutes(1))
                .header("Accept", "application/json")
                .GET()
                .build();
    }

    private static List<User> deserializeUsers(final String json) {
        final var objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
