# 📝 Chamado Service — API de Chamados (Spring Boot + JWT + RabbitMQ)

API responsável pelo **gerenciamento de chamados** no sistema HelpDesk.  
Consome **JWT tokens** emitidos pelo serviço **Usuario Service** para autenticação e validação, publica mensagens na fila do **RabbitMQ**, e fornece documentação e monitoramento completos.

Link do repositório com os manifests para rodar todos os serviços: https://github.com/aoomath/HelpDesk-K8s

---

# ⚡ Visão Geral

Principais responsabilidades do serviço:

- 📝 Criar, atualizar e consultar chamados  
- 🔒 Validar usuários via JWT  
- 🐇 Publicar eventos na fila do RabbitMQ  
- 📚 Documentação via Swagger / OpenAPI  
- 📊 Monitoramento via Actuator

---

# 🛠 Funcionalidades e Infraestrutura

- **Autenticação e Autorização**: JWT  
- **Validação de Dados**: Bean Validation  
- **Tratamento Global de Erros**: `GlobalExceptionHandler`  
- **Banco de Dados**: PostgreSQL com migrations automáticas  
- **Documentação**: Swagger / OpenAPI  
- **Monitoramento**: Actuator endpoints
  - `/actuator/health`  
  - `/actuator/info`  
  - `/actuator/env`  
  - `/actuator/metrics`  
  - `/actuator/loggers`  
- **Mensageria**: RabbitMQ para publicação de eventos  

---

# 🏗️ Tecnologias Principais

| Categoria   | Tecnologias                                      |
|-------------|-------------------------------------------------|
| Linguagem   | Java 21                                         |
| Framework   | Spring Boot 3                                   |
| Segurança   | Spring Security + JWT                           |
| Banco       | PostgreSQL (migrations)                         |
| Comunicação | RabbitMQ                           |
| Testes      | JUnit 5, Mockito, Testcontainers, RestAssuredMockMvc |
| DevOps      | Docker, Kubernetes                              |
| Docs        | Swagger / OpenAPI, Actuator                     |

---

# 🧪 Testes Implementados

- Testes **unitários** com Mockito  
- Testes de **integração** com Testcontainers + RestAssuredMockMvc  
- Mock customizado do JWT token e suas claims  

---

# 🐳 Docker

### Imagem no Docker Hub
[matheusferr/chamado-service](https://hub.docker.com/r/matheusferr/chamado-service)

### Rodando via Docker
```bash
docker pull matheusferr/chamado-service
docker run -p 8081:8081 matheusferr/chamado-service
```

---

# ☸️ Kubernetes

```bash
kubectl apply -R -f .
kubectl port-forward service/chamado-service 8081:8081
```

---

# 💻 Código Fonte

GitHub: [https://github.com/aoomath/chamado-service](https://github.com/aoomath/chamado-service)

---

# 📄 Licença

Distribuído sob MIT License.

---

# ✨ Autor

Matheus A. Ferreira  
GitHub: https://github.com/aoomath
