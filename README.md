# GameVault Backend

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.x-6DB33F.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-3.x-blue.svg)](https://kafka.apache.org/)
[![Prometheus](https://img.shields.io/badge/Prometheus-metrics-E6522C.svg)](https://prometheus.io/)

Backend веб-приложения **GameVault**: учет игр, заметки/рецензии, пользовательские списки, достижения и интеграции (IGDB/Steam).

Frontend находится в отдельном репозитории: [GameVault-frontend](https://github.com/TyanVsharfe/GameVault-frontend)

## Возможности

- Пользователи: логин/логаут (Spring Security, session-based), Remember Me (`remember-me`), регистрация с email-верификацией, сброс пароля по email.
- Игры пользователя: добавление по IGDB ID, статусы (`Playing`, `Played`, `Planned`, `Completed`, `Abandoned`, `None`), `fullyCompleted`, оценки (общая и по режимам), рецензии, фильтрация/пагинация/сортировка.
- Заметки к играм: CRUD заметок по игре (по IGDB ID), массовое удаление всех заметок по игре.
- Кастомные списки: создание/обновление/удаление, копирование списка, порядок игр в списке, массовое удаление игр из списка.
- Достижения: выдача по категориям, локализация, админ-эндпоинт для создания достижений.
- Интеграции: IGDB API, импорт из Steam (асинхронно через Kafka), enriched-эндпоинты с учетом пользовательских данных.
- Наблюдаемость: Spring Boot Actuator + метрики Prometheus.

## Стек

- Java 17
- Spring Boot 3.4.x (Web, Security, Data JPA, Validation, Actuator)
- PostgreSQL (для локального dev через Docker Compose)
- Apache Kafka (очередь задач импорта из Steam)
- Springdoc OpenAPI (Swagger UI)
- Micrometer + Prometheus

## Быстрый старт (Docker Compose)

Требования: Docker + Docker Compose.

1. Запуск инфраструктуры и backend:

```bash
docker compose up --build
```

2. Полезные адреса:

- Backend API: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Prometheus: `http://localhost:9090`
- Kafka UI: `http://localhost:8081`

По умолчанию `docker-compose.yaml` использует `env_file: .env-dev`. Значения в `.env-dev` предназначены для локальной разработки. Не коммитьте реальные секреты в публичные репозитории.

## Запуск локально (без Docker)

Требования: JDK 17+, Maven, PostgreSQL, Kafka (либо их аналоги в Docker).

1. Экспортируйте переменные окружения (см. ниже).
2. Запуск:

```bash
./mvnw spring-boot:run
```

На Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

## Конфигурация

Основной префикс API настраивается в `api.prefix` (по умолчанию `/api/v1`).

Ключевые переменные окружения:

```text
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres
SPRING_DATASOURCE_USERNAME=...
SPRING_DATASOURCE_PASSWORD=...

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# IGDB
IGDB_CLIENT_ID=...
IGDB_CLIENT_SECRET=...

# Steam
STEAM_API_KEY=...

# Email (SMTP, используется для верификации/сброса пароля)
GMAIL_CLIENT_USERNAME=...
GMAIL_CLIENT_PASSWORD=...

# Frontend URL (используется для ссылок в письмах и редиректов)
FRONTEND_URL=http://localhost:3000
```

## Аутентификация

Backend использует session-based аутентификацию (Spring Security). Большинство эндпоинтов требуют авторизации.

- `POST ${api.prefix}/users/login`
- `POST ${api.prefix}/users/logout`
- `GET  ${api.prefix}/users/check-session`
- Remember Me: параметр `remember-me` при логине

Регистрация и восстановление доступа:

- `POST ${api.prefix}/users/registration` (после регистрации требуется подтверждение email)
- `POST ${api.prefix}/users/auth/registration/verify?token=...`
- `POST ${api.prefix}/users/reset-password`
- `POST ${api.prefix}/users/auth/reset-password/verify`

## API (часть эндпоинтов)

Базовый префикс:

```text
${api.prefix}/...
```

### Пользователи

| Метод | Эндпоинт                               | Описание |
|------:|----------------------------------------|----------|
| POST  | `/users/registration`                  | Регистрация (с email-верификацией) |
| POST  | `/users/login`                         | Логин |
| POST  | `/users/logout`                        | Логаут |
| GET   | `/users/check-session`                 | Проверка активной сессии |
| POST  | `/users/reset-password`                | Инициация сброса пароля |
| POST  | `/users/auth/registration/verify`      | Верификация регистрации по `token` |
| POST  | `/users/auth/reset-password/verify`    | Подтверждение сброса пароля |

### Игры пользователя

| Метод  | Эндпоинт                                   | Описание |
|-------:|--------------------------------------------|----------|
| GET    | `/users/games`                              | Список игр (фильтрация/пагинация) |
| GET    | `/users/games/{igdb-id}`                    | Получить игру по IGDB ID |
| POST   | `/users/games/{igdb-id}`                    | Добавить игру |
| PUT    | `/users/games/{igdb-id}`                    | Полное обновление игры |
| PATCH  | `/users/games/{igdb-id}/status`             | Обновить статус |
| PATCH  | `/users/games/{igdb-id}/fully-completed`    | Обновить `fullyCompleted` |
| PATCH  | `/users/games/{igdb-id}/rating`             | Обновить общую оценку |
| PATCH  | `/users/games/{igdb-id}/review`             | Обновить рецензию |
| PUT    | `/users/games/{igdb-id}/modes/{mode}`       | Обновить данные по режиму игры |
| PATCH  | `/users/games/{igdb-id}/modes/{mode}/rating`| Обновить оценку режима |
| GET    | `/users/games/{igdb-id}/reviews`            | Рецензии пользователей на игру |
| DELETE | `/users/games/{igdb-id}`                    | Удалить игру |
| GET    | `/users/games/exists/{igdb-id}`             | Проверить наличие игры |

### Заметки

| Метод  | Эндпоинт                     | Описание |
|-------:|------------------------------|----------|
| GET    | `/games/{igdb-id}/notes`     | Получить заметки по игре |
| POST   | `/games/{igdb-id}/notes`     | Добавить заметку |
| PUT    | `/games/notes/{note-id}`     | Обновить заметку |
| DELETE | `/games/notes/{note-id}`     | Удалить заметку |
| DELETE | `/games/{igdb-id}/all`       | Удалить все заметки по игре |

### Кастомные списки

| Метод  | Эндпоинт                                   | Описание |
|-------:|--------------------------------------------|----------|
| GET    | `/users/game-lists`                        | Получить списки пользователя |
| GET    | `/users/game-lists/{list-id}`              | Получить список по UUID |
| POST   | `/users/game-lists`                        | Создать список |
| POST   | `/users/game-lists/{list-id}/copy`         | Копировать список |
| PUT    | `/users/game-lists/{list-id}`              | Обновить список |
| PUT    | `/users/game-lists/{list-id}/order`        | Обновить порядок игр |
| DELETE | `/users/game-lists/{list-id}`              | Удалить список |
| DELETE | `/users/game-lists/{list-id}/games/{game-id}` | Удалить игру из списка |
| DELETE | `/users/game-lists/{list-id}/games`        | Массовое удаление игр из списка |

### Достижения

| Метод | Эндпоинт                              | Описание |
|------:|----------------------------------------|----------|
| GET   | `/users/achievements`                  | Достижения пользователя (учитывает язык запроса) |
| GET   | `/users/achievements/category/{category}` | Достижения по категории |
| POST  | `/users/achievements`                  | Создать достижение (ADMIN) |

### Импорт из Steam

| Метод | Эндпоинт                  | Описание |
|------:|---------------------------|----------|
| GET   | `/steam-import/{steam-id}`| Получить список игр пользователя Steam |
| POST  | `/steam-import/{steam-id}`| Запустить импорт выбранных игр (Kafka) |

### Enriched API

| Метод | Эндпоинт                                | Описание |
|------:|------------------------------------------|----------|
| GET   | `/games/enriched/search`                 | Поиск игр с учетом пользовательских данных |
| GET   | `/games/enriched/{igdbId}`               | Игра с учетом пользовательских данных |
| GET   | `/games/enriched/game-lists/{list-id}`   | Список с учетом пользовательских данных |
| GET   | `/games/enriched/steam-import/{steam-id}`| Импорт Steam + обогащение |

## Метрики (Prometheus)

- Метрики: `GET /actuator/prometheus`
- Конфиг Prometheus: [src/main/resources/prometheus.yml](src/main/resources/prometheus.yml)



