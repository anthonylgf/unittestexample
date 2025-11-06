package com.example.unittestexample.exceptions;

import com.example.unittestexample.dtos.MensagemErroIndividual;
import com.example.unittestexample.dtos.RespostaErro;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(AlunoExisteMesmoNomeException.class)
  public ResponseEntity<RespostaErro> alunoExisteMesmoNomeException(
      AlunoExisteMesmoNomeException e) {
    log.error("Ja existe aluno com o nome indicado", e);

    var mensagemErro =
        MensagemErroIndividual.builder()
            .mensagem(e.getMessage())
            .codigoErro("ALUNO_EXISTE_MESMO_NOME")
            .build();

    var respostaErro = new RespostaErro();
    respostaErro.setErros(List.of(mensagemErro));

    return ResponseEntity.status(HttpStatus.CONFLICT).body(respostaErro);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(AlunoNaoEncontradoException.class)
  public ResponseEntity<RespostaErro> alunoNaoEncontradoException(AlunoNaoEncontradoException e) {
    log.error(e.getMessage(), e);

    var mensagemErro =
        MensagemErroIndividual.builder()
            .mensagem(e.getMessage())
            .codigoErro("ALUNO_NAO_ENCONTRADO")
            .build();

    var respostaErro = new RespostaErro();
    respostaErro.setErros(List.of(mensagemErro));

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respostaErro);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ParametrosListagemInvalidosException.class)
  public ResponseEntity<RespostaErro> parametrosListagemInvalidosException(
      ParametrosListagemInvalidosException e) {
    log.error("Parametros incorretos ao realizar o GET", e);

    var mensagemErro =
        MensagemErroIndividual.builder()
            .mensagem(e.getMessage())
            .codigoErro("PARAMETROS_LISTAGEM_INVALIDOS")
            .build();

    var respostaErro = new RespostaErro();
    respostaErro.setErros(List.of(mensagemErro));

    return ResponseEntity.badRequest().body(respostaErro);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IdadeInvalidaException.class)
  public ResponseEntity<RespostaErro> exception(IdadeInvalidaException e) {
    log.error("Erro ao processar idade", e);

    var mensagemErro =
        MensagemErroIndividual.builder()
            .mensagem(e.getMessage())
            .codigoErro("IDADE_INVALIDA")
            .build();

    var respostaErro = new RespostaErro();
    respostaErro.setErros(List.of(mensagemErro));

    return ResponseEntity.badRequest().body(respostaErro);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<RespostaErro> methodArgumentNotValidException(MethodArgumentNotValidException e) {
    log.error("Erro ao receber parametros de mensagens", e);
    RespostaErro respostaErro = new RespostaErro();
    respostaErro.setErros(
        e.getBindingResult().getFieldErrors().stream()
            .map(
                fieldError ->
                    MensagemErroIndividual.builder()
                        .codigoErro("CAMPO_INVALIDO")
                        .mensagem(fieldError.getDefaultMessage())
                        .build())
            .toList());
    return ResponseEntity.badRequest().body(respostaErro);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<RespostaErro> httpMessageNotReadableException(HttpMessageNotReadableException e) {
    log.error("Erro ao deserializar mensagem", e);
    RespostaErro respostaErro = new RespostaErro();
    respostaErro.setErros(
        List.of(
            MensagemErroIndividual.builder()
                .codigoErro("ERRO_DE_FORMATACAO_DADOS_INCORRETOS")
                .mensagem(e.getMessage())
                .build()));
    return ResponseEntity.badRequest().body(respostaErro);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  ResponseEntity<RespostaErro> methodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    log.error("Erro ao receber parametros de mensagens", e);
    RespostaErro respostaErro = new RespostaErro();
    respostaErro.setErros(
        List.of(
            MensagemErroIndividual.builder()
                .codigoErro("PARAMETRO_INCORRETO")
                .mensagem(e.getMessage())
                .build()));

    return ResponseEntity.badRequest().body(respostaErro);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MissingServletRequestParameterException.class)
  ResponseEntity<RespostaErro> missingServletRequestParameterException(
      MissingServletRequestParameterException e) {
    log.error("Erro ao receber parametros de mensagens", e);
    RespostaErro respostaErro = new RespostaErro();
    respostaErro.setErros(
        List.of(
            MensagemErroIndividual.builder()
                .codigoErro("PARAMETRO_OBRIGATORIO_NAO_INFORMADO")
                .mensagem(e.getMessage())
                .build()));

    return ResponseEntity.badRequest().body(respostaErro);
  }

  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  ResponseEntity<RespostaErro> methodNotSupportedException(
      HttpRequestMethodNotSupportedException e) {
    log.error("Utilizando método indevido no endpoint", e);

    var metodosPermitidos = Objects.requireNonNullElse(e.getSupportedMethods(), new String[0]);
    var metodoUtilizado = e.getMethod();

    RespostaErro respostaErro = new RespostaErro();
    respostaErro.setErros(
        List.of(
            MensagemErroIndividual.builder()
                .codigoErro("METODO_NAO_PERMITIDO")
                .mensagem(
                    String.format(
                        "Metodo %s nao suportado para o endpoint. Os disponiveis sao %s",
                        metodoUtilizado, Arrays.toString(metodosPermitidos)))
                .build()));

    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(respostaErro);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  ResponseEntity<RespostaErro> exception(Exception e) {
    log.error("Erro inesperado", e);
    RespostaErro respostaErro = new RespostaErro();
    respostaErro.setErros(
        List.of(
            MensagemErroIndividual.builder()
                .codigoErro("ERRO_INTERNO")
                .mensagem(e.getMessage())
                .build()));
    return ResponseEntity.internalServerError().body(respostaErro);
  }
}
