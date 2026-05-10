package com.example.app.feature.book.data

import android.util.Log
import com.example.app.core.network.BASE_URL
import com.example.app.core.storage.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.timeout
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable

private const val TAG = "BookApi"

@Serializable
data class BookDto(
    val id: Int,
    val title: String,
    val description: String,
    val author: String,
    val year: Int,
    val fileUrl: String,
    val coverUrl: String?,
    val userId: Int
)

class BookApi(
    private val client: HttpClient,
    private val tokenStorage: TokenStorage
){

    private suspend fun getToken(): String = tokenStorage.token.first() ?: ""

    suspend fun getAllBooks(): List<BookDto>{
        return client.get("$BASE_URL:8084/api/v1/books"){
            bearerAuth(getToken())
        }.body()
    }

    suspend fun getBookById(id: Int): BookDto {
        return client.get("$BASE_URL:8084/api/v1/books/$id") {
            bearerAuth(getToken())
        }.body()
    }

    suspend fun deleteBook(id: Int) {
        client.delete("$BASE_URL:8084/api/v1/books/$id") {
            bearerAuth(getToken())
        }
    }

    suspend fun streamBook(id: Int): ByteArray {
        Log.d(TAG, "Streaming book $id from $BASE_URL:8084/api/v1/books/$id/stream")
        return try {
            val data = client.get("$BASE_URL:8084/api/v1/books/$id/stream") {
                bearerAuth(getToken())
                timeout {
                    requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
                    socketTimeoutMillis  = 120_000L
                }
            }.body<ByteArray>()
            Log.d(TAG, "Successfully streamed ${data.size} bytes for book $id")
            data
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stream book $id: ${e.message}", e)
            throw e
        }
    }
}