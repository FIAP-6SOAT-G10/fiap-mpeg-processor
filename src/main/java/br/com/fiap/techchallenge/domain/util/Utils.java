package br.com.fiap.techchallenge.domain.util;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public class Utils {

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
}
