package de.hu.kotlin.coroutines

import de.hu.java.http.JsonPlaceHolderClient
import de.hu.java.http.user.User
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.concurrent.CompletableFuture

val jsonPlaceHolderClient = JsonPlaceHolderClient()

/**
 * Three ways to GET a user from https://jsonplaceholder.typicode.com/users
 * and print the response in JSON format in Kotlin.
 * All three approaches use the HttpClient that was released in Java 11.
 */
private const val USER_1_URL = "https://jsonplaceholder.typicode.com/users/1"

fun main() {
    val client: HttpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.NEVER)
        .connectTimeout(Duration.ofSeconds(20))
        .build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create(USER_1_URL))
        .GET()
        .build()

    println(getWithCompletableFuture(client, request))
    println("=".repeat(80))
    println(getWithCoroutines(client, request))
    println("=".repeat(80))
    println(getAllUsersWithCoroutines())
}

// =============================================================================
// Use CompletableFuture to make an asynchronous call to get the users.
fun getWithCompletableFuture(client: HttpClient, request: HttpRequest): String {
    val body: CompletableFuture<String> = client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply { response: HttpResponse<String> -> response.body() }
    return body.get()
}

// =============================================================================
// Use Kotlin coroutines to make an asynchronous call to get the users.
// Actually, the caller will be blocked until the result is available.
// However, the network call is non-blocking and releases the thread until the
// result is available.
fun getWithCoroutines(client: HttpClient, request: HttpRequest): String = runBlocking {
    // Note that Deferred is Kotlin's equivalent of Java CompletableFuture.
    val result: Deferred<HttpResponse<String>> = async(Dispatchers.IO) {
        fetchUrl(client, request)
    }
    val data: HttpResponse<String> = result.await() // Await the result of Kotlin Deferred
    data.body()
}

suspend fun fetchUrl(client: HttpClient, request: HttpRequest): HttpResponse<String> {
    val response: CompletableFuture<HttpResponse<String>> = client.sendAsync(
        request, HttpResponse.BodyHandlers.ofString()
    )
    return response.await() // Await the result of the Java CompletionStage
}

// =============================================================================
// Use Kotlin coroutines to make an asynchronous call to get all users with
// the Java JsonPlaceholderClient.
// Actually, the caller will be blocked until the result is available.
// However, the network call is non-blocking and releases the thread until the
// result is available.
fun getAllUsersWithCoroutines(): List<User> = runBlocking {
    val result: Deferred<List<User>> = async(Dispatchers.IO) {
        jsonPlaceHolderClient.getUsers()
    }
    result.await()
}
