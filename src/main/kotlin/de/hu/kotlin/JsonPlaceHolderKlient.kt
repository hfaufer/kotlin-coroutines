package de.hu.kotlin

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import de.hu.java.http.user.User
import java.io.UncheckedIOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.concurrent.CompletableFuture

fun main() {
    val client = JsonPlaceHolderKlient()
    CompletableFuture.runAsync { println("It works with Java 25!") }
        .thenCompose { client.getUsersAsync() }
        .thenApply { users -> println(users) }
        .join()
}

class JsonPlaceHolderKlient {

    private val objectMapper = ObjectMapper()

    companion object {

        private val BASE_URL: URI = URI.create("https://jsonplaceholder.typicode.com")
    }

    fun getUsersAsync(): CompletableFuture<List<User>> =
        newHttp2Client().use { client ->
            return client.sendAsync(getUsersRequest(), HttpResponse.BodyHandlers.ofString())
                .thenApply { response -> deserializeUsers(response.body()) }
        }

    private fun newHttp2Client(): HttpClient =
        HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NEVER)
            .connectTimeout(Duration.ofSeconds(20))
            .build()

    private fun getUsersRequest(): HttpRequest =
        HttpRequest.newBuilder()
            .uri(BASE_URL.resolve("/users"))
            .timeout(Duration.ofMinutes(1))
            .header("Accept", "application/json")
            .GET()
            .build()

    private fun deserializeUsers(json: String?): List<User> =
        try {
            return objectMapper.readValue(json, object : TypeReference<List<User>>() {})
        } catch (e: JsonProcessingException) {
            throw UncheckedIOException(e)
        }

}
