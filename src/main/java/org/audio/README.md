# Audio Module

Модуль для поиска, идентификации и регистрации аудиотреков по аудиофайлам с использованием аудиофингерпринтов.

## Возможности
- Идентификация трека по аудиофайлу
- Поиск похожих треков
- Регистрация новых треков в базе

## Сборка и запуск

### Требования
- Java 21 (или 17)
- Gradle


### Сборка
```sh
./gradlew build
```

### Запуск
```sh
./gradlew bootRun
```

Приложение будет доступно на http://localhost:8080

## API

### 1. Идентификация трека
```sh
curl -X POST -F "audioFile=@путь_к_файлу.mp3" http://localhost:8080/api/audio/identify
```

### 2. Поиск похожих треков
```sh
curl -X POST -F "audioFile=@путь_к_файлу.mp3" "http://localhost:8080/api/audio/top-similar?limit=3&minConfidence=0.01"
```

### 3. Регистрация трека
```sh
curl -X POST \
  -F "trackId=track123" \
  -F "title=Название трека" \
  -F "audioFile=@путь_к_файлу.mp3" \
  http://localhost:8080/api/audio/register
```

## Тестирование и покрытие

- Запуск всех тестов:
  ```sh
  ./gradlew test
  ```
- Покрытие кода можно посмотреть через IntelliJ IDEA (Run with Coverage)

## Структура проекта
- `src/main/java/org/audio/` — основной код модуля
- `src/test/java/org/audio/` — юнит-тесты

## Зависимости
- Spring Boot
- JUnit 5
- Mockito
- FFmpeg (должен быть установлен в системе)

## Авторы
- [Ваше имя или команда] 