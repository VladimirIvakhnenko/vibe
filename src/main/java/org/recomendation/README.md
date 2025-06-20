# Vibe Recommendation System

## Описание

Vibe — это прототип рекомендательной системы для музыкальных треков, использующий графовую модель связей между треками и учитывающий лайки/дизлайки пользователей. Система реализована на Java с использованием Spring Boot и JGraphT.

## Архитектура

- **Графовая модель:**
  - Узлы — треки (`Track`), содержащие id, название, жанр, исполнителя.
  - Рёбра — связи между треками (общий жанр, исполнитель, лайки одного пользователя).
  - Вес ребра увеличивается при лайках, уменьшается при дизлайках.
- **In-memory хранение:**
  - Все данные (граф, предпочтения пользователей) хранятся в памяти (для прототипа).
- **REST API:**
  - Получение рекомендаций, лайк/дизлайк трека.

## Структура пакетов

```
org.recomendation/
  configs/         # Spring-конфигурация и тестовые данные
  controller/      # REST API (RecommendationController)
  db/              # In-memory граф и репозиторий предпочтений
  dto/             # DTO для API (LikeRequest, RecommendationResponse)
  models/          # Сущности (Track, UserPreference)
  services/        # Бизнес-логика (LikeService, RecommendationService)
  utils/           # Вспомогательные классы (по необходимости)
```

## Основные классы
- **Track** — музыкальный трек (id, title, genre, artist)
- **TrackGraph** — граф треков на JGraphT
- **UserPreference** — лайки/дизлайки пользователя
- **LikeService** — обработка лайков/дизлайков, обновление графа
- **RecommendationService** — алгоритм рекомендаций (BFS, сортировка, фильтрация)
- **RecommendationController** — REST API

## REST API

### Получить рекомендации
```
GET /api/recommendations?user_id=USER_ID&limit=10
```
**Ответ:**
```json
{
  "tracks": [
    { "id": "t1", "title": "Bohemian Rhapsody", "genre": "Rock", "artist": "Queen" },
    ...
  ]
}
```

### Лайк трека
```
POST /api/recommendations/like
Content-Type: application/json

{
  "userId": "user1",
  "trackId": "t1"
}
```

### Дизлайк трека
```
POST /api/recommendations/dislike
Content-Type: application/json

{
  "userId": "user1",
  "trackId": "t2"
}
```

## Пример сценария работы
1. Пользователь лайкает несколько треков.
2. Система увеличивает веса связей между этими треками.
3. При запросе рекомендаций система ищет связанные треки (BFS, глубина 2), сортирует по весу, исключает дизлайкнутые.
4. Возвращает топ-N рекомендаций.

## Запуск
1. Убедитесь, что установлен JDK 17+ и Gradle Wrapper работает.
2. Соберите и запустите приложение:
   ```
   ./gradlew.bat build
   ./gradlew.bat bootRun
   ```
3. API будет доступно по адресу: `http://localhost:8080/api/recommendations`

## Тестирование и покрытие
- Для запуска тестов и генерации отчёта о покрытии (JaCoCo):
  ```
  ./gradlew.bat test jacocoTestReport
  ```
- Отчёт будет доступен в `build/jacocoHtml/index.html`

## Примечания
- Все данные (граф, лайки/дизлайки) сбрасываются при перезапуске (in-memory).
- Для реального проекта можно заменить in-memory на БД и расширить алгоритмы рекомендаций. 