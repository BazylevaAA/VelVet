# VelVet — Медиа-платформа для музыки, видео и книг

> Автор: **Bazyleva Anastasia**

VelVet — это полноценная медиа-платформа с микросервисной backend-архитектурой на Kotlin/Ktor и Android-клиентом на Jetpack Compose. Платформа позволяет загружать, хранить и стримить музыку, видео и книги с авторизацией через JWT.

---

## Содержание

- [Технологии](#технологии)
- [Архитектура](#архитектура)
- [Схема базы данных](#схема-базы-данных)
- [Сборка и запуск](#сборка-и-запуск)
- [API Reference](#api-reference)
- [Конфигурация](#конфигурация)

---

## Технологии

### Backend

| Категория | Технология | Версия |
|---|---|---|
| Язык | Kotlin | 2.3.0 |
| Фреймворк | Ktor | 3.1.0 / 3.3.2 |
| Сборка | Gradle (Kotlin DSL) | — |
| JVM | Java | 21 |
| База данных | PostgreSQL | 16 (Alpine) |
| ORM | Jetbrains Exposed | 0.61.0 |
| Аутентификация | JWT (Ktor auth-jwt) | — |
| Хранилище файлов | MinIO (S3-совместимое) | — |
| Логирование | Logback | 1.4.14 |
| HTTP-клиент | Ktor Client | — |
| Хэширование паролей | JBCrypt | 0.4 |

### Android-клиент

| Категория | Технология | Версия |
|---|---|---|
| Язык | Kotlin | — |
| UI-фреймворк | Jetpack Compose | — |
| Навигация | Navigation Compose | 2.7.7 |
| DI | Koin | 3.5.3 |
| HTTP-клиент | Ktor Client | 2.3.9 |
| Сериализация | Kotlinx Serialization JSON | — |
| Локальное хранилище | DataStore Preferences | — |
| Загрузка изображений | Coil | 2.6.0 |
| Аудио/Видео плеер | Media3 ExoPlayer | 1.3.1 |
| EPUB-ридер | AndroidX WebKit | 1.12.1 |
| Min SDK | API 26 (Android 8.0) | — |
| Target SDK | API 36 | — |

### Инфраструктура

| Технология | Назначение |
|---|---|
| Docker & Docker Compose | Контейнеризация всех сервисов |
| MinIO | S3-совместимое объектное хранилище |
| PostgreSQL (×4) | Отдельная БД на каждый сервис |

---

## Архитектура

Проект состоит из четырёх независимых микросервисов и Android-клиента:

```
VelVet/
├── backend/
│   ├── auth-service/      # Аутентификация и управление пользователями  (порт 8081)
│   ├── music-service/     # Загрузка и стриминг музыки                  (порт 8082)
│   ├── video-service/     # Загрузка и стриминг видео                   (порт 8083)
│   ├── book-service/      # Загрузка и чтение книг (PDF/EPUB)           (порт 8084)
│   └── postgres/          # SQL-скрипты инициализации БД
├── client/
│   └── app/               # Android-приложение (MVVM + Clean Architecture)
└── docker-compose.yml     # Оркестрация всего стека
```

### Паттерн клиентского приложения

Клиент реализован по архитектуре **MVVM + Clean Architecture** с модулями по фичам:

```
feature/
├── auth/    # Регистрация / Вход
├── home/    # Главный экран
├── music/   # Плеер и список треков
├── video/   # Видеоплеер и список видео
└── book/    # Ридер и список книг
```

---

## Схема базы данных

### Auth Service (`velvet_auth`)

```sql
CREATE TABLE users (
    id           SERIAL PRIMARY KEY,
    email        VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name         VARCHAR(100) NOT NULL,
    created_at   TIMESTAMP DEFAULT NOW()
);
```

### Music Service (`velvet_music`)

```sql
CREATE TABLE tracks (
    id         SERIAL PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    artist     VARCHAR(255) NOT NULL,
    album      VARCHAR(255),
    duration   INT NOT NULL,
    file_url   VARCHAR(500) NOT NULL,
    cover_url  VARCHAR(500),
    user_id    INT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);
```

### Video Service (`velvet_video`)

```sql
CREATE TABLE videos (
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    director    VARCHAR(255),
    year        INT NOT NULL,
    duration    INT NOT NULL,
    genre       VARCHAR(100),
    file_url    VARCHAR(500) NOT NULL,
    cover_url   VARCHAR(500),
    user_id     INT NOT NULL,
    created_at  TIMESTAMP DEFAULT NOW()
);
```

### Book Service (`velvet_book`)

```sql
CREATE TABLE book (
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    author      VARCHAR(255) NOT NULL,
    year        INT NOT NULL,
    file_url    VARCHAR(255) NOT NULL,
    cover_url   VARCHAR(255) NOT NULL,
    user_id     INT NOT NULL,
    created_at  TIMESTAMP DEFAULT NOW()
);
```

---

## Сборка и запуск

### Требования

- **Docker** и **Docker Compose** (рекомендуемый способ)
- **JDK 21** (для локального запуска сервисов)
- **Android Studio** (для сборки клиента)

---

### Запуск всего стека через Docker Compose (рекомендуется)

```bash
# Клонировать репозиторий
git clone <repo-url>
cd VelVet

# Поднять все сервисы
docker-compose up

# Остановить
docker-compose down
```

После запуска доступны:

| Сервис | URL |
|---|---|
| Auth Service | http://localhost:8081 |
| Music Service | http://localhost:8082 |
| Video Service | http://localhost:8083 |
| Book Service | http://localhost:8084 |
| MinIO Console | http://localhost:9001 |
| MinIO API | http://localhost:9000 |

---

### Локальная сборка отдельного сервиса

Каждый сервис — самостоятельный Gradle-проект. Команды одинаковы для всех:

```bash
cd backend/auth-service   # или music-service, video-service, book-service

# Запустить тесты
./gradlew test

# Собрать JAR
./gradlew buildFatJar

# Собрать Docker-образ
./gradlew buildImage

# Запустить локально
./gradlew run

# Запустить Docker-образ
./gradlew runDocker
```

---

### Сборка Android-клиента

```bash
cd client

# Собрать APK
./gradlew build

# Установить на подключённое устройство / эмулятор
./gradlew installDebug
```

> Минимальная версия Android: **API 26 (Android 8.0)**

---

## API Reference

Все эндпоинты, кроме `/register` и `/login`, требуют заголовок:

```
Authorization: Bearer <JWT_TOKEN>
```

---

### Auth Service — `http://localhost:8081`

#### Регистрация

```
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "secret123",
  "name": "Анастасия"
}
```

**Ответ `200 OK`:**
```json
{
  "token": "<JWT>"
}
```

---

#### Вход

```
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "secret123"
}
```

**Ответ `200 OK`:**
```json
{
  "token": "<JWT>"
}
```

---

#### Получить текущего пользователя

```
GET /api/v1/auth/me
Authorization: Bearer <JWT>
```

**Ответ `200 OK`:**
```json
{
  "id": 1,
  "email": "user@example.com",
  "name": "Анастасия"
}
```

---

### Music Service — `http://localhost:8082`

#### Загрузить трек

```
POST /api/v1/tracks/upload
Authorization: Bearer <JWT>
Content-Type: multipart/form-data

Fields:
  file    — аудиофайл (audio/mpeg)
  cover   — обложка (image/*)
  title   — название трека
  artist  — исполнитель
  album   — альбом (необязательно)
  duration — длительность в секундах
```

**Ответ `201 Created`:**
```json
{
  "id": 1,
  "title": "Track Name",
  "artist": "Artist",
  "album": "Album",
  "duration": 210,
  "fileUrl": "http://...",
  "coverUrl": "http://..."
}
```

---

#### Получить все треки пользователя

```
GET /api/v1/tracks
Authorization: Bearer <JWT>
```

**Ответ `200 OK`:**
```json
[
  {
    "id": 1,
    "title": "Track Name",
    "artist": "Artist",
    "album": "Album",
    "duration": 210,
    "fileUrl": "http://...",
    "coverUrl": "http://..."
  }
]
```

---

#### Получить трек по ID

```
GET /api/v1/tracks/{id}
Authorization: Bearer <JWT>
```

---

#### Удалить трек

```
DELETE /api/v1/tracks/{id}
Authorization: Bearer <JWT>
```

**Ответ `200 OK`**

---

#### Стримить трек

```
GET /api/v1/tracks/{id}/stream
Authorization: Bearer <JWT>
```

**Ответ:** бинарный поток `audio/mpeg`

---

### Video Service — `http://localhost:8083`

> Максимальный размер загружаемого файла: **2 ГБ**

#### Загрузить видео

```
POST /api/v1/videos/upload
Authorization: Bearer <JWT>
Content-Type: multipart/form-data

Fields:
  file        — видеофайл (video/mp4)
  cover       — обложка (image/*)
  title       — название
  description — описание (необязательно)
  director    — режиссёр (необязательно)
  year        — год выпуска
  duration    — длительность в секундах
  genre       — жанр (необязательно)
```

**Ответ `201 Created`:**
```json
{
  "id": 1,
  "title": "Video Title",
  "description": "...",
  "director": "Director",
  "year": 2024,
  "duration": 5400,
  "genre": "Drama",
  "fileUrl": "http://...",
  "coverUrl": "http://..."
}
```

---

#### Получить все видео пользователя

```
GET /api/v1/videos
Authorization: Bearer <JWT>
```

---

#### Получить видео по ID

```
GET /api/v1/videos/{id}
Authorization: Bearer <JWT>
```

---

#### Удалить видео

```
DELETE /api/v1/videos/{id}
Authorization: Bearer <JWT>
```

**Ответ `200 OK`**

---

#### Стримить видео

```
GET /api/v1/videos/{id}/stream
Authorization: Bearer <JWT>
```

**Ответ:** бинарный поток `video/mp4`

---

### Book Service — `http://localhost:8084`

#### Загрузить книгу

```
POST /api/v1/books/upload
Authorization: Bearer <JWT>
Content-Type: multipart/form-data

Fields:
  file        — файл книги (PDF или EPUB)
  cover       — обложка (image/*)
  title       — название
  description — описание
  author      — автор
  year        — год издания
```

**Ответ `201 Created`:**
```json
{
  "id": 1,
  "title": "Book Title",
  "description": "...",
  "author": "Author Name",
  "year": 2023,
  "fileUrl": "http://...",
  "coverUrl": "http://..."
}
```

---

#### Получить все книги пользователя

```
GET /api/v1/books
Authorization: Bearer <JWT>
```

---

#### Получить книгу по ID

```
GET /api/v1/books/{id}
Authorization: Bearer <JWT>
```

---

#### Удалить книгу

```
DELETE /api/v1/books/{id}
Authorization: Bearer <JWT>
```

**Ответ `200 OK`**

---

#### Стримить / читать книгу

```
GET /api/v1/books/{id}/stream
Authorization: Bearer <JWT>
```

**Ответ:** бинарный поток `application/pdf` или `application/epub+zip`

---

## Конфигурация

### JWT (единая для всех сервисов)

| Параметр | Значение |
|---|---|
| `jwt.secret` | `TMP2lGzHgGcJvKMasHYoKVVz7MIjBzIelqq4n4wHAD7` |
| `jwt.issuer` | `velvet-auth` |
| `jwt.audience` | `velvet-users` |
| `jwt.realm` | `velvet` |

### MinIO

| Параметр | Значение |
|---|---|
| Endpoint (локально) | `http://localhost:9000` |
| Endpoint (Docker) | `http://minio:9000` |
| Access Key | `minioadmin` |
| Secret Key | `minioadmin` |
| Console | `http://localhost:9001` |

### Базы данных

| Сервис | Порт (хост) | БД |
|---|---|---|
| auth-service | 5432 | `velvet_auth` |
| music-service | 5433 | `velvet_music` |
| video-service | 5434 | `velvet_video` |
| book-service | 5435 | `velvet_book` |

Логин/пароль для всех БД: `postgres` / `postgres`
