package br.com.fiap.techchallenge.application.usecases;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class ProcessorOutUseCase {

    private static String FOLDER_PROCESSED = "processed/";
    private static String FILENAME = "files-compressed.zip";

    @Value("${aws.s3.bucket.out.name}")
    private String bucketOut;

    public void consumerFilesInBucket(String prefix, AmazonS3 s3) {

        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest().withBucketName(bucketOut).withPrefix(prefix +"/images/"));
        String pastaDestino = "";

        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            if (objectSummary.getSize() == 0) {
                continue;
            }
            log.info(" - " + objectSummary.getKey() + "  (size = " + objectSummary.getSize() + ")");

            String chaveOrigem = objectSummary.getKey();
            String nomeArquivo = chaveOrigem.substring(chaveOrigem.lastIndexOf("/") + 1);
            pastaDestino = chaveOrigem.substring(0, chaveOrigem.lastIndexOf("/") + 1) + FOLDER_PROCESSED ;
            String chaveDestino = pastaDestino + nomeArquivo;

            try {
                s3.copyObject(bucketOut, chaveOrigem, bucketOut, chaveDestino);
                log.info("Arquivo copiado para: " + chaveDestino);
                s3.deleteObject(bucketOut, chaveOrigem);
                log.info("Arquivo excluído: " + chaveOrigem);
            } catch (AmazonServiceException e) {
                System.err.println("Erro ao copiar ou excluir o arquivo: " + e.getMessage());
            } catch (SdkClientException e) {
                System.err.println("Erro de cliente ao acessar o S3: " + e.getMessage());
            }
        }
        zipAllFiles(s3, prefix);
    }

    public void zipAllFiles(AmazonS3 s3Client, String prefix) {
        try {
            File zipFile = new File(FILENAME);
            try (FileOutputStream fos = new FileOutputStream(zipFile);
                 ZipOutputStream zipOut = new ZipOutputStream(fos)) {

                ObjectListing result = s3Client.listObjects(new ListObjectsRequest().withBucketName(bucketOut).withPrefix(prefix + "/images/processados/"));
                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    String key = objectSummary.getKey();
                    String chaveOrigem = objectSummary.getKey();
                    String nomeArquivo = chaveOrigem.substring(chaveOrigem.lastIndexOf("/") + 1);
                    S3Object s3Object = s3Client.getObject(bucketOut, key);
                    try (S3ObjectInputStream s3InputStream = s3Object.getObjectContent()) {
                        ZipEntry zipEntry = new ZipEntry(nomeArquivo);
                        zipOut.putNextEntry(zipEntry);
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = s3InputStream.read(buffer)) != -1) {
                            zipOut.write(buffer, 0, bytesRead);
                        }
                        zipOut.closeEntry();
                    }
                }
            }

            log.info("Arquivo ZIP criado: " + zipFile.getAbsolutePath());
            String zipKey = prefix + "/compressed-file/" + FILENAME;
            s3Client.putObject(bucketOut, zipKey, zipFile);
            log.info("Arquivo ZIP enviado para o S3: " + zipKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
