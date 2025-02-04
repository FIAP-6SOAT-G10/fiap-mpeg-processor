# Processor Microservice

## Visão Geral
Este projeto é uma aplicação de microsserviço desenvolvida em Java, Spring Boot e Maven. Ele processa arquivos de vídeo armazenados em um bucket AWS S3, extrai frames e envia notificações via AWS SQS.

## Funcionalidades
- **Processamento de Vídeo**: Extrai frames de vídeos MP4 armazenados em um bucket S3.
- **Integração com AWS**: Utiliza AWS S3 para armazenamento e AWS SQS para mensagens.
- **MongoDB**: Armazena detalhes de usuários e arquivos em um banco de dados MongoDB.
- **Spring Boot**: Utiliza Spring Boot para configuração da aplicação e gerenciamento de dependências.

## Tecnologias
- Java 17
- Spring Boot 3.3.5
- Maven
- AWS S3
- AWS SQS
- MongoDB
- Lombok
- OpenCV
- FFmpeg

## Começando

### Pré-requisitos
- Java 17
- Maven
- Docker (para MongoDB)

### Instalação
1. **Clone o repositório**:
    ```sh
    git clone https://github.com/FIAP-6SOAT-G10/fiap-mpeg-processor.git
    cd fiap-hackaton
    ```

2. **Inicie o MongoDB**:
    ```sh
    docker run -d -p 27017:27017 --name mongodb mongo:latest
    ```

3. **Configure as credenciais da AWS**:
   Certifique-se de que suas credenciais da AWS estão configuradas em `~/.aws/credentials`.

4. **Construa o projeto**:
    ```sh
    mvn clean install
    ```

5. **Execute a aplicação**:
    ```sh
    mvn spring-boot:run
    ```

## Configuração
A configuração é gerenciada através de arquivos YAML localizados no diretório `src/main/resources`.

### `application.yml`
Configurações gerais da aplicação.

### `application-local.yml`
Configurações do ambiente local, incluindo MongoDB e AWS.

## Variáveis de Ambiente
Certifique-se de definir as seguintes variáveis de ambiente:
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_SESSION_TOKEN` (se aplicável)

## Uso
A aplicação escuta uma fila SQS para solicitações de processamento de vídeo. Quando uma mensagem é recebida, ela processa o vídeo, extrai frames e os envia para um bucket S3. Em seguida, envia uma notificação com os resultados do processamento.

## Estrutura do Projeto
- `br.com.fiap.techchallenge`: Pacote principal.
- `application`: Contém casos de uso e gateways.
- `domain`: Contém enums e utilitários.
- `infra`: Contém configuração, pontos de entrada, exceções, gateways, mapeadores e implementações de repositórios.

## Testes
Para executar os testes unitários:
```sh
mvn test -P unit-test
