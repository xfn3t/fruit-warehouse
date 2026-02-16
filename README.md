# Fruit Warehouse Management System

## О проекте

Fruit Warehouse Management System представляет собой REST API для управления складом фруктов, построенное на Spring Boot. Система предоставляет функционал для управления поставщиками, продуктами, ценами, доставками и генерации отчетов в различных форматах.

## Архитектура и технологии

### Технологический стек

- **Java 17** с Spring Boot 3.x
- **PostgreSQL 15**
- **Spring Data JPA**
- **Liquibase**
- **Docker и Docker Compose**
- **Swagger/OpenAPI 3**
- **MapStruct**
- **Lombok**
- **iTextPDF**
- **Testcontainers**

## Быстрый старт

### Способ 1: Запуск с помощью Docker Compose (рекомендуется)

1. Клонируйте репозиторий:
```
git clone <repository-url>
cd fruit-warehouse
```

2. Запустите приложение:
```
docker-compose up -d
```

3. Приложение будет доступно по адресу: http://localhost:8080

### Способ 2: Ручной запуск

1. Настройте базу данных PostgreSQL:
```sql
CREATE DATABASE fruitwarehouse;
CREATE USER fruituser WITH PASSWORD 'fruitpass';
GRANT ALL PRIVILEGES ON DATABASE fruitwarehouse TO fruituser;
```

2. Соберите и запустите приложение:
```
mvn clean package
java -jar target/fruitwarehouse-0.0.1-SNAPSHOT.jar
```

## Структура базы данных

### Основные сущности

- **suppliers** — поставщики фруктов
- **products** — продукты (фрукты) с типами и сортами
- **product_types** — типы продуктов (яблоки, груши и т.д.)
- **supplier_product_prices** — цены поставщиков на продукты с периодом действия
- **deliveries** — доставки от поставщиков
- **delivery_items** — позиции в доставке
- **delivery_statuses** — статусы доставок

### Пользовательские домены PostgreSQL

- `price_domain` — DECIMAL(10,2) CHECK (value >= 0)
- `weight_domain` — DECIMAL(10,3) CHECK (value >= 0.001)

## API Endpoints

### Документация API

После запуска приложения документация доступна по адресам:
- Swagger UI: http://localhost:8080/swagger-ui/index.html

### Основные endpoints

#### 1. Управление доставками (/api/v1/deliveries)

- `POST /api/v1/deliveries` — создать новую доставку
- `GET /api/v1/deliveries/{id}` — получить доставку по ID
- `GET /api/v1/deliveries` — получить все доставки
- `GET /api/v1/deliveries/supplier/{supplierId}` — получить доставки поставщика

#### 2. Управление ценами поставщиков (/api/v1/suppliers/{supplierId}/prices)

- `POST /api/v1/suppliers/{supplierId}/prices` — добавить/обновить цену
- `GET /api/v1/suppliers/{supplierId}/prices` — получить все цены поставщика
- `GET /api/v1/suppliers/{supplierId}/prices/active` — получить активные цены
- `DELETE /api/v1/suppliers/{supplierId}/prices/{priceId}` — удалить цену

#### 3. Отчеты (/api/v1/reports)

- `GET /api/v1/reports` — сгенерировать отчет по доставкам

## Примеры запросов

### 1. Создание доставки

```
curl -X POST "http://localhost:8080/api/v1/deliveries" \
  -H "Content-Type: application/json" \
  -d '{
    "supplierId": 1,
    "deliveryDate": "2024-01-15T10:00:00",
    "items": [
      {
        "productId": 1,
        "weight": 100.5
      },
      {
        "productId": 2,
        "weight": 75.3
      }
    ]
  }'
```

### 2. Добавление цены поставщика

```
curl -X POST "http://localhost:8080/api/v1/suppliers/1/prices" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "price": 45.50,
    "effectiveFrom": "2024-01-01",
    "effectiveTo": "2024-12-31"
  }'
```

### 3. Генерация отчета

```
# JSON отчет (по умолчанию)
curl "http://localhost:8080/api/v1/reports?startDate=2024-01-01&endDate=2024-01-31&detailed=true"

# PDF отчет
curl "http://localhost:8080/api/v1/reports?startDate=2024-01-01&endDate=2024-01-31&detailed=false&format=PDF" -o report.pdf
```

### 4. Получение активных цен поставщика

```
curl "http://localhost:8080/api/v1/suppliers/1/prices/active"
```

## Тестирование

### Запуск тестов

```
# Все тесты
mvn test

# Интеграционные тесты
mvn test -Dtest="*IntegrationTest"

# Unit тесты
mvn test -Dtest="*UnitTest"
```

### Структура тестов

- **Unit тесты** — тестирование отдельных компонентов
- **Интеграционные тесты** — тестирование с реальной БД через Testcontainers
- **E2E тесты** — тестирование полного потока через REST API

## Docker конфигурация

### Docker Compose файл включает

1. **Приложение Spring Boot** — порт 8080
2. **PostgreSQL 15** — порт 5432
3. **Пользовательские домены БД** — автоматически создаются при запуске

### Переменные окружения

```yaml
POSTGRES_DB: fruitwarehouse
POSTGRES_USER: fruituser
POSTGRES_PASSWORD: fruitpass
SPRING_PROFILES_ACTIVE: docker
```

## Генерация отчетов

### Поддерживаемые форматы

1. **JSON** — для интеграции с другими системами
2. **PDF** — для печати и презентаций

### Типы отчетов

- **Сводный отчет** — группировка по поставщику, типу продукта и сорту
- **Детальный отчет** — полная информация по каждой позиции доставки

### Пример ответа JSON отчета

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "detailed": false,
  "summaryItems": [
    {
      "supplierName": "ООО Фруктовый рай",
      "productType": "Apple",
      "variety": "Golden",
      "totalWeight": 1250.75,
      "totalCost": 5628.38
    }
  ],
  "totalWeight": 1250.75,
  "totalCost": 5628.38
}
```

## Конфигурация

### Основные настройки в application.yaml

```
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/fruitwarehouse
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: none
    properties:
      hibernate:
        default_schema: fruitwarehouse
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    show-sql: true

  liquibase:
    liquibase-schema: public
    default-schema: fruitwarehouse
    change-log: classpath:/db/changelog/db.changelog-master.yaml

```

## Обработка ошибок

Система предоставляет структурированные ответы об ошибках.

### Пример ошибки

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Supplier with id 999 not found",
  "path": "/api/v1/suppliers/999/prices"
}
```

### Типы исключений

- `EntityNotFoundException` — сущность не найдена (404)
- `ValidationException` — ошибка валидации (400)
- `PriceConflictException` — конфликт цен (409)
- `ConstraintViolationException` — нарушение ограничений БД (400)

## Структура проекта

```
src/main/java/com/fruitwarehouse/
├── common/                 # Общие компоненты
│   └── exception/         # Исключения и обработчики
├── delivery/              # Модуль доставок
│   ├── controller/        # REST контроллеры
│   ├── entity/           # JPA сущности
│   ├── repository/       # Репозитории
│   ├── service/          # Бизнес-логика
│   └── mapper/           # Мапперы DTO
├── product/              # Модуль продуктов
├── supplier/             # Модуль поставщиков
├── report/               # Модуль отчетов
└── FruitWarehouseApplication.java
```

## Зависимости Maven

Ключевые зависимости:

- `spring-boot-starter-web` — REST API
- `spring-boot-starter-data-jpa` — работа с БД
- `spring-boot-starter-validation` — валидация
- `postgresql` — драйвер PostgreSQL
- `liquibase-core` — миграции БД
- `mapstruct` — маппинг объектов
- `lombok` — сокращение кода
- `springdoc-openapi-starter-webmvc-ui` — документация API
- `itext7-core` — генерация PDF
- `testcontainers` — интеграционное тестирование

## Бизнес-правила

### Доставки

1. Для создания доставки должны быть заданы активные цены у поставщика
2. Вес товара должен быть больше 0.001 кг
3. Каждая доставка получает уникальный UUID номер
4. По умолчанию доставка создается со статусом "CREATED"

### Цены

1. Цены действуют в определенный период (effectiveFrom - effectiveTo)
2. Периоды цен не могут пересекаться
3. Цена должна быть положительной
4. Если effectiveTo не указан, цена действует бессрочно

### Отчеты

1. Максимальный период отчета — 1 год
2. Дата начала не может быть позже даты окончания
3. Дата начала не может быть в будущем
