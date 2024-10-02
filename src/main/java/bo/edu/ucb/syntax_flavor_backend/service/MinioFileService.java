package bo.edu.ucb.syntax_flavor_backend.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.errors.MinioException;
import jakarta.annotation.PostConstruct;

@Service
public class MinioFileService {

    Logger LOGGER = LoggerFactory.getLogger(MinioFileService.class);

    private final MinioClient minioClient;
    private final String bucketName;

    public MinioFileService(MinioClient minioClient, @Value("${spring.minio.bucket-name}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }
    
    //@PostConstruct
    public void initBucket() {
        try {
            // Check connection by listing buckets
            LOGGER.info("Checking Minio connection...");
            minioClient.listBuckets();
            LOGGER.info("Minio connection successful.");

            // Check permissions by creating and deleting a test bucket
            String testBucketName = "test-bucket-" + System.currentTimeMillis();
            LOGGER.info("Checking Minio permissions by creating a test bucket: {}", testBucketName);
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(testBucketName).build());
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(testBucketName).build());
            LOGGER.info("Minio permissions check successful.");

            // Initialize the actual bucket
            LOGGER.info("Initializing Minio bucket: {}", bucketName);
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                LOGGER.info("Bucket not found. Creating bucket: {}", bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            } else {
                LOGGER.info("Bucket already exists: {}", bucketName);
            }
        } catch (Exception e) {
            LOGGER.error("Error initializing Minio bucket", e);
            throw new RuntimeException("Error initializing Minio bucket", e);
        }
}

    public String uploadFile(String fileName, byte[] fileData, String contentType) {
        try (ByteArrayInputStream fileStream = new ByteArrayInputStream(fileData)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(fileStream, fileData.length, -1)
                    .contentType(contentType)
                    .build());

            // Construct the URL manually
            String minioBaseUrl = "http://localhost:9000"; // Use the appropriate port (9000 or 9001)
            return minioBaseUrl + "/" + bucketName + "/" + fileName; // Constructed URL for the uploaded file
        } catch (MinioException e) {
            throw new RuntimeException("MinIO error during upload: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("IO error during upload: " + e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No such algorithm error: " + e.getMessage(), e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid key error: " + e.getMessage(), e);
        }
    }

    public String uploadPdf(String fileName, byte[] pdfData) {
        try (ByteArrayInputStream pdfStream = new ByteArrayInputStream(pdfData)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(pdfStream, pdfData.length, -1)
                    .contentType("application/pdf") // Set content type to PDF
                    .build());

            // Construct the URL manually
            String minioBaseUrl = "http://localhost:9000"; // Use the appropriate port (9000 or 9001)
            return minioBaseUrl + "/" + bucketName + "/" + fileName; // Constructed URL for the uploaded file
        } catch (MinioException e) {
            throw new RuntimeException("MinIO error during upload: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("IO error during upload: " + e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No such algorithm error: " + e.getMessage(), e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid key error: " + e.getMessage(), e);
        }
    }

    // Function to get s single file from Minio
    public byte[] getFile(String objectName) {
        try {
            GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName) // Use just the object name here
                    .build());
            return response.readAllBytes();
        } catch (MinioException e) {
            throw new RuntimeException("Error during Minio download", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid Minio credentials", e);
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file from Minio", e);
        }
    }


}
