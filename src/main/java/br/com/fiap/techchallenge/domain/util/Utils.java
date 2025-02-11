package br.com.fiap.techchallenge.domain.util;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.Date;

public class Utils {

    private static final String TINY_URL = "http://tinyurl.com/api-create.php?url=";
    private static final String TINY_URL_ENCODE = "UTF-8";

    public static AmazonS3 connectAmazonS3(String region) {
        return AmazonS3Client.builder()
                .withRegion(Regions.fromName(region))
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();
    }

    public static AmazonSQS connectAmazonSQS(String region) {
        return AmazonSQSClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .build();
    }

    public static File convertS3ObjectToFile(S3Object s3Object, Path tempDir, String fileName) throws IOException {
        File file = new File(tempDir.toFile(), fileName);
        try (S3ObjectInputStream s3InputStream = s3Object.getObjectContent();
             FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = s3InputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
        return file;
    }

    public static String generateUrlpreAssigned(String bucketName, String keyName, AmazonS3 s3Client) throws IOException {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, keyName);
        generatePresignedUrlRequest.setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000));
        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        return generateShortUrl(url.toString());
    }

    private static String generateShortUrl(String longUrl) throws IOException {
        URL url = new URL(TINY_URL + URLEncoder.encode(longUrl, TINY_URL_ENCODE));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
}
