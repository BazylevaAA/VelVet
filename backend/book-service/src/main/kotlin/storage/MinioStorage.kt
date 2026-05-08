package storage

import io.ktor.server.application.Application
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import java.net.URI
import java.time.Duration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.HeadBucketRequest
import software.amazon.awssdk.services.s3.model.NoSuchBucketException
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.InputStream

object MinioStorage {

    private lateinit var s3Client: S3Client
    private lateinit var bucket: String

    fun init(application: Application){
        val endpoint = application.environment.config.property("minio.endpoint").getString()
        val accessKey = application.environment.config.property("minio.accessKey").getString()
        val secretKey = application.environment.config.property("minio.secretKey").getString()
        bucket = application.environment.config.property("minio.bucket").getString()

        val credentials = AwsBasicCredentials.create(accessKey, secretKey)

        s3Client = S3Client.builder()
            .endpointOverride(URI.create(endpoint))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.US_EAST_1)
            .serviceConfiguration(
                S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .chunkedEncodingEnabled(false)
                    .build()
            )
            .httpClientBuilder(
                ApacheHttpClient.builder()
                    .connectionTimeout(Duration.ofSeconds(30))
                    .socketTimeout(Duration.ofMinutes(15))
            )
            .build()

        createBucketIfNotExists()
    }

    private fun createBucketIfNotExists(){
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build())
        } catch (e: NoSuchBucketException) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build())
        }
    }

    fun uploadFile(
        key: String,
        inputStream: InputStream,
        contentType: String,
        size: Long,
    ): String {
        s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).contentType(contentType).build(),
            RequestBody.fromInputStream(inputStream, size))

        return "${getEndpoint()}/$bucket/$key"
    }

    fun deleteFile(key: String){
        s3Client.deleteObject(
            DeleteObjectRequest.builder().bucket(bucket).key(key).build()
        )
    }

    fun getFile(key: String): InputStream {
        return s3Client.getObject(
            GetObjectRequest.builder().bucket(bucket).key(key).build(),
        )
    }

    private fun getEndpoint() = s3Client.serviceClientConfiguration().endpointOverride().get().toString().trimEnd('/')
}