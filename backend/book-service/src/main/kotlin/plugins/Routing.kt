package plugins

import com.example.dto.request.CreateBookRequest
import com.example.exception.BookException
import com.example.repository.BookRepository
import com.example.service.BookService
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondOutputStream
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun Application.configureRoutong() {
    val bookService = BookService(BookRepository())

    routing {
        route("/api/v1") {
            authenticate("auth-jwt") {
                post("/books/upload") {
                    val userId = call.principal<JWTPrincipal>()!!
                        .payload.getClaim("userId").asInt()

                    val multipart = call.receiveMultipart()

                    var request: CreateBookRequest? = null
                    var fileBytes: ByteArray? = null
                    var fileContentType = "application/pdf"
                    var coverBytes: ByteArray? = null
                    var coverContentType: String? = null

                    multipart.forEachPart { part ->
                        when {
                            part is PartData.FormItem && part.name == "data" -> {
                                request = Json.decodeFromString(part.value)
                            }
                            part is PartData.FileItem && part.name == "file" -> {
                                fileContentType = part.contentType?.toString() ?: "application/pdf"
                                fileBytes = part.streamProvider().readBytes()
                            }
                            part is PartData.FileItem && part.name == "cover" -> {
                                coverContentType = part.contentType?.toString()
                                coverBytes = part.streamProvider().readBytes()
                            }
                        }
                        part.dispose()
                    }

                    val req = request
                        ?: throw BookException(HttpStatusCode.BadRequest, "Missing 'data' field")
                    val file = fileBytes
                        ?: throw BookException(HttpStatusCode.BadRequest, "Missing 'file' file")

                    val response = bookService.uploadBook(
                        request = req,
                        fileStream = file.inputStream(),
                        fileSize = file.size.toLong(),
                        fileContentType = fileContentType,
                        coverStream = coverBytes?.inputStream(),
                        coverSize = coverBytes?.size?.toLong(),
                        coverContentType = coverContentType,
                        userId = userId
                    )

                    call.respond(HttpStatusCode.Created, response)
                }

                get("/books") {
                    val userId = call.principal<JWTPrincipal>()!!
                        .payload.getClaim("userId").asInt()
                    call.respond(bookService.getAllBooks(userId))
                }

                get("/books/{id}") {
                    val userId = call.principal<JWTPrincipal>()!!
                        .payload.getClaim("userId").asInt()
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: throw BookException(HttpStatusCode.BadRequest, "Invalid book id")
                    call.respond(bookService.getBookById(id, userId))
                }

                delete("/books/{id}") {
                    val userId = call.principal<JWTPrincipal>()!!
                        .payload.getClaim("userId").asInt()
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: throw BookException(HttpStatusCode.BadRequest, "Invalid book id")
                    bookService.deleteBook(id, userId)
                    call.respond(HttpStatusCode.NoContent)
                }

                get("/books/{id}/stream") {
                    val userId = call.principal<JWTPrincipal>()!!
                        .payload.getClaim("userId").asInt()
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: throw BookException(HttpStatusCode.BadRequest, "Invalid book id")

                    val book = bookService.getBookById(id, userId)
                    val contentType = when {
                        book.fileUrl.endsWith(".pdf") -> ContentType.Application.Pdf
                        book.fileUrl.endsWith(".epub") -> ContentType("application", "epub+zip")
                        else -> ContentType.Application.OctetStream
                    }

                    val stream = bookService.streamBook(id)
                    call.respondOutputStream(contentType) {
                        stream.use { it.copyTo(this) }
                    }
                }
            }
        }
    }
}
