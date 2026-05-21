# Stock Tracker

Aplicação para monitoramento de ações (Stocks) e Fundos Imobiliários (FIIs) da B3.

## Tecnologias

- Java 21
- Spring Boot 3.4
- MongoDB
- WebFlux (WebClient para integrações)
- BRAPI (API de cotações)

## Funcionalidades

- **Cadastro de ativos**: busca informações de ações e FIIs via código (ticker) e classifica automaticamente
- **Compra e venda**: registra transações e mantém histórico completo
- **Preço médio**: calcula automaticamente o preço médio ponderado das compras

## Pré-requisitos

- Java 21+
- MongoDB rodando em `localhost:27017`
- (Opcional) Token da [BRAPI](https://brapi.dev) para cotações

## Como rodar

```bash
# Sem token BRAPI (funciona com limite de requisições)
./mvnw spring-boot:run

# Com token BRAPI
BRAPI_TOKEN=seu_token ./mvnw spring-boot:run

# Ou via variável de ambiente do MongoDB
MONGODB_URI=mongodb://localhost:27017/stocktracker ./mvnw spring-boot:run
```

## API Endpoints

### Assets

| Método | Endpoint              | Descrição                        |
|--------|-----------------------|----------------------------------|
| POST   | `/api/assets`         | Cadastrar ativo por ticker       |
| GET    | `/api/assets`         | Listar todos os ativos           |
| GET    | `/api/assets?type=FII`| Filtrar por tipo (STOCK ou FII)  |
| GET    | `/api/assets/{ticker}`| Buscar ativo por ticker          |

### Transactions

| Método | Endpoint                      | Descrição                          |
|--------|-------------------------------|------------------------------------|
| POST   | `/api/transactions`           | Registrar compra ou venda          |
| GET    | `/api/transactions/{ticker}`  | Histórico de transações por ticker |

### Exemplos

**Cadastrar ativo:**
```json
POST /api/assets
{ "ticker": "PETR4" }
```

**Registrar compra:**
```json
POST /api/transactions
{
  "ticker": "PETR4",
  "type": "BUY",
  "quantity": 100,
  "pricePerUnit": 35.50
}
```

**Registrar venda:**
```json
POST /api/transactions
{
  "ticker": "PETR4",
  "type": "SELL",
  "quantity": 50,
  "pricePerUnit": 38.00
}
```
