# Invoice/Transfer job

Esse serviço tem a responsabilidade de integrar com a plataforma e SDK da StarkBank, gerando Invoices e, ao ser notificado que houve um pagamento de Invoice, deve realizar uma transferencia para uma conta bancaria.

## Configuração de Ambiente

Por questões de segurança, as credenciais da Stark Bank não são versionadas.
Crie um arquivo `.env` na raiz do projeto contendo suas chaves:

```properties
STARKBANK_PROJECT_ID=seu-project-id-aqui
STARKBANK_PRIVATE_KEY=-----BEGIN EC PRIVATE KEY-----\...\n-----END EC PRIVATE KEY-----
STARKBANK_ENVIRONMENT=sandbox
```

---

## Como Executar

### Pré-requisitos
* **Docker** instalado e rodando.
* Arquivo `.env` configurado na raiz do projeto (conforme tópico anterior).

### Passo a Passo

1. **Construa a Imagem:**

   ```bash
    docker build -t invoice-job .
   ```
2. **Rode a imagem:**
    
    ```bash
   docker run -p 8080:8080 --env-file .env invoice-job
    ```
   
3. **Inicie o ngrok**
    ```bash
   ngrok http 8080
    ```

Assim que o container iniciar:

* O Scheduler começará a emitir Invoices aleatórias.
* A API estará pronta para receber Webhooks em https://xxx-ngrok-free.dev/api/webhook/invoices

---

## Executar Testes

Existem testes para ambos os Services de Invoice e Transfer e tambem testes para o WebhookController.

Para rodar os testes basta rodar o seguinte comando na raiz do projeto:
```bash
./gradlew test
```

---

## Observações finais

Embora esta aplicação atenda aos requisitos funcionais do desafio, a lista abaixo destaca as evoluções para proximos passos:

### 1. Persistência Distribuída
Atualmente o banco utilizado esta sendo o H2 (In-memory). Se fossemos escalar, perderiamos dados ja que cada Pod armazenaria apenas uma parte do todo. Seria interessante implementarmos um PostgreSQL para lidarmos com race condition, duplicidades e escalabilidade.

### 2. Processamento Assíncrono & DLQ
Poderiamos alterar o processamento síncrono utilizando um Kafka/RabbitMQ ou outros para que o Webhook apenas recebesse o evento e esse, fosse publicado na fila. Garantindo uma DLQ para eventuais falhas e reprocessamento e maior garantia contra perda de dados. 

### 3. Key Vault
Atualmente estou criando localmente um arquivo .env sem nenhuma fonte da verdade. Deveria ser colocado algum tipo de secret vault para obtermos as variaveis de ambiente.

### 4. Controle de bloqueio
O banco de dados em memoria foi para simular (e tratar, nesse caso) a questao de duplicatas porem, o certo seriamos termos um Redis na frente para a checagem de Invoice/Transfer duplicados antes mesmo do banco de dados fazer os registros.

### 5. Observabilidade e Tracing
As logs presentes no serviço sao apenas para observação local. Em ambiente produtivo deveria ser implementado logs, metricas e logs. Poderiamos utilizar OpenTelemetry junto com Grafana para termos uma melhor observabilidade.

### 6. CI/CD
O build esta sendo feito localmente e manual. Deve ser criadas pipelines automatizadas, rodando testes, analises e checks para vulnerabilidade.

### 7. Kubernetes
Apesar de estar rodando em Docker, poderiamos escalar isso usando Helm para deploy no Kubernetes e termos um ambiente digno para rodar a aplicação