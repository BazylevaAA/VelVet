package exception

import io.ktor.http.HttpStatusCode

class VideoException(
    val statusCode: HttpStatusCode,
    override val message: String
): Exception(message)