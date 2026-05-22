# Stock Tracker

Aplicação para monitoramento de ações (Stocks) e Fundos Imobiliários (FIIs) da B3.

## Tecnologias

- Java 21
- Spring Boot 3.4
- MongoDB
- WebFlux (WebClient para integrações)
- Apache POI (exportação Excel)
- Jsoup (web scraping)

## Fontes de Dados

O app busca informações de múltiplas fontes, agregando dados automaticamente:

| Fonte | Dados | Observações |
|-------|-------|-------------|
| [BRAPI](https://brapi.dev) | Preço, nome, moeda, logo | Fonte primária. Alguns tickers exigem token |
| [Status Invest](https://statusinvest.com.br) | CNPJ, razão social, dividend yield | Via web scraping |
| [Yahoo Finance](https://finance.yahoo.com) | Preço, dividend yield, dividendos anuais | API pública |

## Funcionalidades

- **Cadastro de ativos**: busca informações de ações e FIIs via ticker, incluindo nome da empresa e CNPJ
- **Classificação automática**: STOCK ou FII baseado no padrão do ticker
- **Compra e venda**: registra transações e mantém histórico completo
- **Preço médio**: calcula automaticamente o preço médio ponderado
- **Relatório de IR**: endpoint com dados para declaração de imposto de renda (CSV/Excel)
- **Rendimentos**: dividend yield, renda estimada mensal/anual, posição na carteira

## Pré-requisitos

- Java 21+
- MongoDB rodando em `localhost:27017`
- (Opcional) Token da [BRAPI](https://brapi.dev) para cotações

## Como rodar

```bash
./mvnw spring-boot:run

# Com token BRAPI
BRAPI_TOKEN=seu_token ./mvnw spring-boot:run

# Com MongoDB customizado
MONGODB_URI=mongodb://localhost:27017/stocktracker ./mvnw spring-boot:run
```

## API Endpoints

### Assets

| Método | Endpoint              | Descrição                                    |
|--------|-----------------------|----------------------------------------------|
| POST   | `/api/assets`         | Cadastrar ativo (retorna CNPJ + nome empresa)|
| GET    | `/api/assets`         | Listar todos (filtro: `?type=FII`)           |
| GET    | `/api/assets/{ticker}`| Buscar por ticker                            |

### Transactions

| Método | Endpoint                      | Descrição                          |
|--------|-------------------------------|------------------------------------|
| POST   | `/api/transactions`           | Registrar compra ou venda          |
| GET    | `/api/transactions/{ticker}`  | Histórico de transações por ticker |

### Relatório de IR

| Método | Endpoint                    | Descrição                              |
|--------|-----------------------------|----------------------------------------|
| GET    | `/api/reports/tax`          | Relatório JSON (param: `?year=2025`)   |
| GET    | `/api/reports/tax/csv`      | Exportar CSV                           |
| GET    | `/api/reports/tax/excel`    | Exportar Excel (.xlsx)                 |

O relatório inclui: ticker, empresa, CNPJ, tipo, quantidade e valor no ano anterior, quantidade e valor no ano de competência, preço médio, total comprado/vendido.

### Rendimentos

| Método | Endpoint              | Descrição                                  |
|--------|-----------------------|--------------------------------------------|
| GET    | `/api/yield`          | Resumo de rendimentos de toda a carteira   |
| GET    | `/api/yield/{ticker}` | Rendimentos de um ativo específico         |

Retorna: dividend yield, valor estimado mensal/anual, posição na carteira (%), yield on cost.

### Exemplos

**Cadastrar ativo:**
```json
POST /api/assets
{ "ticker": "PETR4" }
```

**Relatório de IR (ano 2025):**
```
GET /api/reports/tax?year=2025
GET /api/reports/tax/csv?year=2025
GET /api/reports/tax/excel?year=2025
```

**Rendimentos da carteira:**
```
GET /api/yield
GET /api/yield/PETR4
```
