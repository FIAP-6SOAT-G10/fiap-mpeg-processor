package br.com.fiap.techchallenge.infra.entrypoints.bucket;

import br.com.fiap.techchallenge.application.gateways.IProcessor;
import br.com.fiap.techchallenge.application.gateways.IUserDetailRepository;
import br.com.fiap.techchallenge.application.gateways.IUserRepository;
import br.com.fiap.techchallenge.application.usecases.AmazonSQSUseCase;
import br.com.fiap.techchallenge.application.usecases.ProcessorOutUseCase;
import br.com.fiap.techchallenge.domain.enums.StatusEnum;
import br.com.fiap.techchallenge.domain.util.Utils;
import br.com.fiap.techchallenge.infra.exception.FileMalFormedException;
import br.com.fiap.techchallenge.infra.exception.FormatFileException;
import br.com.fiap.techchallenge.infra.repository.dto.FileResponse;
import br.com.fiap.techchallenge.infra.repository.dto.FileResponseDetail;
import br.com.fiap.techchallenge.infra.repository.dto.UserDTO;
import br.com.fiap.techchallenge.infra.repository.dto.UserDetailDTO;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static br.com.fiap.techchallenge.domain.enums.ErrosEnum.ERRO_PARAMETRO;
import static br.com.fiap.techchallenge.domain.enums.ErrosEnum.FILE_PROCESSED_ERROR;
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

    public ProcessorFilesS3(IUserRepository userRepository, IUserDetailRepository detailRepository, ProcessorOutUseCase processorOut, AmazonSQSUseCase amazonSQSUseCase) {
        this.userRepository = userRepository;
        this.detailRepository = detailRepository;
        this.processorOut = processorOut;
        this.amazonSQSUseCase = amazonSQSUseCase;
    }

    @Override
    public void processor(UserDTO user) throws IOException {
        log.info("ProcessorFilesS3:: processor {}", user);
        processFileOnBucket(bucketIn, user);
    }

    public void processFileOnBucket(String bucketName, UserDTO user) throws IOException {
        log.info("ProcessorFilesS3:processFileOnBucket  bucketName {}  user {}  ", bucketName, user);
        AmazonS3 s3Client = connectAmazonS3(region);
        String prefix = user.getProtocol().toString();
        ObjectListing objectListing = s3Client.listObjects(new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix));
        log.info("ProcessorFilesS3:: total de arquivos encontrados {}", objectListing.getObjectSummaries().size());

        Path tempDir = Files.createTempDirectory("s3_temp_files");

        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {

                String chaveOrigem = objectSummary.getKey();
                String nomeArquivo = chaveOrigem.substring(chaveOrigem.lastIndexOf("/") + 1);
                ObjectMetadata metadata = s3Client.getObjectMetadata(bucketName, chaveOrigem);
                String contentType = metadata.getContentType();
                UserDetailDTO detail = new UserDetailDTO(nomeArquivo, StatusEnum.SUCESS.getNominalStatus(), user, "");

            try {

                if (objectSummary.getSize() == 0) {
                    continue;
                }

                if (fileType.equalsIgnoreCase(contentType)) {
                    log.info("Encontrado um arquivo MP4: " + objectSummary.getKey());
                    user.setFilename(nomeArquivo);
                    userRepository.saveUser(user);
                    executeFile(chaveOrigem, nomeArquivo, s3Client, prefix, user, tempDir);
                } else {
                    throw new FormatFileException(ERRO_PARAMETRO);
                }
                //s3Client.deleteObject(bucketIn, chaveOrigem);
                log.info("Arquivo excluído: " + chaveOrigem);
            } catch (FileMalFormedException e) {
                detail.setStatus(StatusEnum.ERROR.getNominalStatus());
                detail.setError( e.getError().getMessage());
            }
            detailRepository.saveUserDetail(detail);
        }

        String pathFile = processorOut.consumerFilesInBucket(prefix, s3Client);
        String urlpreAssigned = Utils.generateUrlpreAssigned(bucketOut, pathFile, s3Client);
        completeAndDispatchToQueue(user, pathFile, urlpreAssigned);
    }

    private void executeFile(String chaveOrigem, String nomeArquivo, AmazonS3 s3Client, String prefix, UserDTO user, Path tempDir) throws IOException {
        log.info("ProcessorFilesS3:executeFile  chaveOrigem {}  nomeArquivo {} s3Client {} prefix {} user {} tempDir {}", chaveOrigem, nomeArquivo, s3Client, prefix, user, tempDir);
        S3Object s3Object = s3Client.getObject(bucketIn, chaveOrigem);
        log.info("Pasta temporária criada: " + tempDir.toAbsolutePath());
        convertS3ObjectToFile(s3Object, tempDir, nomeArquivo);
        processMp4Video(tempDir.toAbsolutePath() + File.separator + nomeArquivo, s3Client, prefix, user, tempDir);
    }

    public void processMp4Video(String videoPath, AmazonS3 s3Client, String prefix, UserDTO user, Path tempDir) throws IOException {
        log.info("ProcessorFilesS3:processMp4Video  videoPath {}  s3Client {} prefix {} user {} tempDir {}", videoPath, s3Client, prefix, user, tempDir);
        long intervalInMilliseconds = 5000;
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath);
        try {
            grabber.setAudioChannels(0);
            grabber.start();
            int frameRate = (int) grabber.getFrameRate();
            long frameInterval = (long) (frameRate * intervalInMilliseconds / 1000);
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
            long frameCount = 0;
            while (frameCount < grabber.getLengthInFrames()) {
                Frame frame = grabber.grabFrame();
                if (frame != null && frameCount % frameInterval == 0) {
                    Mat mat = converter.convert(frame);
                    String filename = "image_" + frameCount + ".png";
                    String imageFileName = tempDir.toAbsolutePath() + File.separator + filename;
                    opencv_imgcodecs.imwrite(imageFileName, mat);
                    log.info("Imagem salva: " + imageFileName);
                    File f = new File(imageFileName);
                    s3Client.putObject(bucketOut, prefix + "/images/" + filename, f);
                }
                frameCount++;
            }
            grabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileMalFormedException(FILE_PROCESSED_ERROR);
        }
    }

    private void completeAndDispatchToQueue(UserDTO user, String pathFile, String urlpreAssigned) throws JsonProcessingException {
        log.info("ProcessorFilesS3:completeAndDispatchToQueue  user {}  pathFile {} ", user, pathFile);



        List<UserDetailDTO> details = detailRepository.findByUser(user);
        List<FileResponseDetail> filesDetail = new ArrayList<>();
        details.forEach(it -> {
            filesDetail.add(FileResponseDetail
                    .builder()
                    .filename(it.getFilename())
                    .status(it.getStatus())
                    .error(it.getError())
                    .build());
        });
        amazonSQSUseCase.prepareToSend(FileResponse
                .builder()
                .recipientEmail(user.getEmail())
                .recipientName(user.getName())
                .url(urlpreAssigned)
                .protocol(user.getProtocol())
                .files(filesDetail)
                .build());
    }
}

