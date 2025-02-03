package br.com.fiap.techchallenge.infra.entrypoints.bucket;

import br.com.fiap.techchallenge.application.gateways.IProcessor;
import br.com.fiap.techchallenge.application.gateways.IUserDetailRepository;
import br.com.fiap.techchallenge.application.gateways.IUserRepository;
import br.com.fiap.techchallenge.application.usecases.AmazonSQSUseCase;
import br.com.fiap.techchallenge.application.usecases.ProcessorOutUseCase;
import br.com.fiap.techchallenge.domain.enums.StatusEnum;
import br.com.fiap.techchallenge.infra.exception.FormatFileException;
import br.com.fiap.techchallenge.infra.repository.dto.FileResponse;
import br.com.fiap.techchallenge.infra.repository.dto.FileResponseDetail;
import br.com.fiap.techchallenge.infra.repository.dto.UserDTO;
import br.com.fiap.techchallenge.infra.repository.dto.UserDetailDTO;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static br.com.fiap.techchallenge.domain.enums.ErrosEnum.ERRO_PARAMETRO;
import static br.com.fiap.techchallenge.domain.util.Utils.*;

@Slf4j
public class ProcessorFilesS3 implements IProcessor {

    @Value("${aws.region.name}")
    private String region;

    @Value("${aws.s3.bucket.in.name}")
    private String bucketIn;

    @Value("${aws.s3.bucket.in.file.type}")
    private String fileType;

    @Value("${aws.s3.bucket.out.name}")
    private String bucketOut;

    private final IUserRepository userRepository;
    private final IUserDetailRepository detailRepository;
    private final ProcessorOutUseCase processorOut;
    private final AmazonSQSUseCase amazonSQSUseCase;

    public ProcessorFilesS3(IUserRepository userRepository, IUserDetailRepository detailRepository,ProcessorOutUseCase processorOut,  AmazonSQSUseCase amazonSQSUseCase ) {
        this.userRepository = userRepository;
        this.detailRepository = detailRepository;
        this.processorOut = processorOut;
        this.amazonSQSUseCase = amazonSQSUseCase;
    }

    @Override
    public void processor(UserDTO user) throws IOException {
        processFileOnBucket(bucketIn, user);
    }

    public void processFileOnBucket(String bucketName, UserDTO user) throws IOException {
        AmazonS3 s3Client = connectAmazonS3(region);
        String prefix = user.getProtocol().toString();
        ObjectListing objectListing = s3Client.listObjects(new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix));

        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {

            if (objectSummary.getSize() == 0) {
                continue;
            }

            String chaveOrigem = objectSummary.getKey();
            String nomeArquivo = chaveOrigem.substring(chaveOrigem.lastIndexOf("/") + 1);
            ObjectMetadata metadata = s3Client.getObjectMetadata(bucketName, chaveOrigem);
            String contentType = metadata.getContentType();

            if (fileType.equalsIgnoreCase(contentType)) {
                log.info("Encontrado um arquivo MP4: " + objectSummary.getKey());

                user.setFilename(nomeArquivo);
                UserDTO updatedUser = userRepository.saveUser(user);

                executeFile(chaveOrigem, nomeArquivo, s3Client, prefix, user);
                completeAndDispatchToQueue(updatedUser);

            }else {
                /** PREPARA O ENVIO PRA FILA E SOBE A EXCEPTION  **/
                throw new FormatFileException(ERRO_PARAMETRO);
            }

            s3Client.deleteObject(bucketIn, chaveOrigem);
            log.info("Arquivo excluído: " + chaveOrigem);

        }
    }

    private void executeFile(String chaveOrigem, String nomeArquivo, AmazonS3 s3Client, String prefix, UserDTO user) throws IOException {

        S3Object s3Object = s3Client.getObject(bucketIn, chaveOrigem);
        Path tempDir = Files.createTempDirectory("s3_temp_files");
        log.info("Pasta temporária criada: " + tempDir.toAbsolutePath());

        // Converter o S3Object para um arquivo local
        convertS3ObjectToFile(s3Object, tempDir, nomeArquivo);
        processMp4Video(tempDir.toAbsolutePath()+ "\\" + nomeArquivo, s3Client, prefix, user, tempDir);
        processorOut.consumerFilesInBucket(prefix, s3Client);
    }

    public void processMp4Video(String videoPath, AmazonS3 s3Client, String prefix, UserDTO user, Path tempDir) throws IOException {

        // Intervalo de 5 segundos entre as capturas
        long intervalInMilliseconds = 5000;
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath);
        try {
            grabber.setAudioChannels(0);
            grabber.start();

            int frameRate = (int )grabber.getFrameRate();
            long frameInterval = (long) (frameRate * intervalInMilliseconds / 1000);

            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
            long frameCount = 0;

            while (frameCount < grabber.getLengthInFrames()) {
                // Pega o frame correspondente ao intervalo
                Frame frame = grabber.grabFrame();
                if (frame != null && frameCount % frameInterval == 0) {
                    Mat mat = converter.convert(frame);
                    String filename = "image_" + frameCount + ".png";
                    String imageFileName = tempDir.toAbsolutePath() + "\\" + filename;
                    opencv_imgcodecs.imwrite(imageFileName, mat);
                    log.info("Imagem salva: " + imageFileName);

                    File f = new File(imageFileName);
                    s3Client.putObject(bucketOut, prefix + "/images/" + filename, f);

                    UserDetailDTO detail = new UserDetailDTO(filename, StatusEnum.SUCESS.getNominalStatus(), user);
                    detailRepository.saveUserDetail(detail);
                }
                frameCount++;

                // Delay de 5 segundos para pegar o próximo frame
                //Thread.sleep(intervalInMilliseconds);
            }
            grabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void completeAndDispatchToQueue(UserDTO user) throws JsonProcessingException {
        List<UserDetailDTO> details = detailRepository.findByUser(user);
        List<FileResponseDetail> filesDetail = new ArrayList<>();
        details.forEach(it -> {
            filesDetail.add(FileResponseDetail
                    .builder()
                    .filename(it.getFilename())
                    .type(it.getStatus())
                    .build());
        });

        amazonSQSUseCase.prepareToSend( FileResponse
                .builder()
                .recipientEmail(user.getEmail())
                .recipientName(user.getName())
                .url(user.getName())
                .protocol(user.getProtocol())
                .files(filesDetail)
                .build());
    }
}

