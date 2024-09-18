package bo.edu.ucb.syntax_flavor_backend.menu.service;

import java.io.IOException;
import java.security.InvalidKeyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.errors.MinioException;
import jakarta.annotation.PostConstruct;

@Service
public class MinioImageService {

    Logger LOGGER = LoggerFactory.getLogger(MinioImageService.class);

    private final MinioClient minioClient;
    private final String bucketName;

    @Autowired
    public MinioImageService(MinioClient minioClient, @Value("${spring.minio.bucket-name}") String bucketName) {
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
        } catch (Exception e) {
            LOGGER.error("Error initializing Minio bucket", e);
            throw new RuntimeException("Error initializing Minio bucket", e);
        }
}

    public String uploadImage(MultipartFile file){
        try {
            String fileName = file.getOriginalFilename();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            return fileName; // TODO: return the URL of the uploaded image
        } catch (MinioException | IOException e) {
            throw new RuntimeException("Error during Minio upload", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid Minio credentials", e);
        } catch (Exception e) {
            throw new RuntimeException("Error uploading image to Minio", e);
        }
    }
    
}
