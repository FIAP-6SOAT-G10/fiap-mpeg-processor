package br.com.fiap.techchallenge.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Level;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrosEnum {

    /* ERROS GENÉRICOS 001 - 99 */
    ERRO_PARAMETRO("001","Não é possivel processar uma arquivo com formato diferente de mp4.", Level.ERROR, HttpStatus.BAD_REQUEST),
    FILE_PROCESSED_ERROR("002","Ocorreu um erro ao tentar processar o arquivo.", Level.ERROR, HttpStatus.BAD_REQUEST),
    ZIP_FILE_PROCESSED_ERROR("003","Ocorreu um erro ao tentar gerar o arquivo compactado", Level.ERROR, HttpStatus.BAD_REQUEST),


    ;
    private final String code;
    private final String message;
    private final Level logLevel;
    private final HttpStatus httpStatusCode;
}
