package br.com.fiap.techchallenge.application.usecases;

import br.com.fiap.techchallenge.infra.repository.dto.FileResponse;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Value;

import static br.com.fiap.techchallenge.domain.util.Utils.connectAmazonSQS;

public class AmazonSQSUseCase {

    @Value("${aws.account.number}")
    private String account;

    @Value("${aws.region.name}")
    private String region;

    @Value("${aws.sqs.notificator.queue}")
    private String queueNotificator;

    public void prepareToSend(FileResponse message) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String urlNotificator = "https://sqs." + region + ".amazonaws.com/" + account + "/" + queueNotificator;
        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(urlNotificator)
                .withMessageBody(ow.writeValueAsString(message));
        sendMessage(sendMessageRequest);
    }

    public void sendMessage(SendMessageRequest sendMessageRequest ) {
        AmazonSQS sqs = connectAmazonSQS(region);
        SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageRequest);
        System.out.println("Mensagem enviada com sucesso. ID: " + sendMessageResult.getMessageId());
    }
}
