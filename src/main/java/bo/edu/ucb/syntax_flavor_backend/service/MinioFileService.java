package bo.edu.ucb.syntax_flavor_backend.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import bo.edu.ucb.syntax_flavor_backend.util.BillGenerationException;
import io.minio.BucketExistsArgs;
import io.minio.GetBucketPolicyArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.SetBucketPolicyArgs;
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
    
    @PostConstruct
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

            // Check if bucket policy is set
            String policy = minioClient.getBucketPolicy(GetBucketPolicyArgs.builder().bucket(bucketName).build());
            if (policy != null && !policy.isEmpty()) {
                LOGGER.info("Bucket policy already set for bucket: {}", bucketName);
            } else {
                LOGGER.info("Bucket policy not set for bucket: {}. Setting policy...", bucketName);
                // Set bucket policy for public read access
                policy = "{\n" +
                        "  \"Version\": \"2012-10-17\",\n" +
                        "  \"Statement\": [\n" +
                        "    {\n" +
                        "      \"Effect\": \"Allow\",\n" +
                        "      \"Principal\": \"*\",\n" +
                        "      \"Action\": \"s3:GetObject\",\n" +
                        "      \"Resource\": \"arn:aws:s3:::" + bucketName + "/*\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(policy)
                        .build());
                LOGGER.info("Bucket policy set for public read access.");
            }
        } catch (Exception e) {
            LOGGER.error("Error initializing Minio bucket", e);
            throw new RuntimeException("Error initializing Minio bucket", e);
        }
}

    public String uploadFile(String fileName, byte[] fileData, String contentType) {
        // FIXME this method should be dynamic
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

    public String uploadPdf(String fileName, byte[] pdfData) throws BillGenerationException {
        // FIXME this method should be dynamic
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
            throw new BillGenerationException("MinIO error during upload: " + e.getMessage(), 2);
        } catch (IOException e) {
            throw new BillGenerationException("IO error during upload: " + e.getMessage(), 2);
        } catch (NoSuchAlgorithmException e) {
            throw new BillGenerationException("No such algorithm error: " + e.getMessage(), 2);
        } catch (InvalidKeyException e) {
            throw new BillGenerationException("Invalid key error: " + e.getMessage(), 2);
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
