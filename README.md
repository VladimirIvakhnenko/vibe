# Прототип системы рекомендаций музыки "Vibe"

Это прототип системы музыкальных рекомендаций, разработанный как часть проекта "Vibe". Система использует графовую модель для представления связей между треками и предпочтениями пользователя, чтобы генерировать персонализированные рекомендации.

## Архитектура

Система построена на **Spring Boot** и использует in-memory графовую библиотеку **JGraphT**.

### Ключевые компоненты:

-   **`TrackGraph`**: Граф, где вершины — это треки (`Track`). При старте приложения граф автоматически заполняется данными из `spotify_1000_tracks...json`. Рёбра представляют собой связи между треками, построенные на основе **общих исполнителей** (вес +1.0) и **общих жанров** (вес +0.5).
-   **`UserPreference`**: Хранилище ID лайкнутых и дизлайкнутых треков.
-   **`RecommendationService`**: Ядро системы. Генерирует рекомендации, используя обход графа в ширину (BFS) от топ-10 лайкнутых треков.
-   **`LikeService`**: Обрабатывает лайки/дизлайки/прослушивания, **динамически обновляя веса** рёбер в `TrackGraph`.
-   **`RecommendationController`**: REST-контроллер, предоставляющий API для взаимодействия с системой.

## API Endpoints

### 1. Лайк трека

Увеличивает вес рёбер, связанных с этим треком, на **+1.0**.

-   **URL**: `/api/recommendations/like`
-   **Method**: `POST`
-   **Body**: `{"trackId": "ID трека из JSON"}`

### 2. Дизлайк трека

Уменьшает вес рёбер, связанных с этим треком, на **-1.0**.

-   **URL**: `/api/recommendations/dislike`
-   **Method**: `POST`
-   **Body**: `{"trackId": "ID трека из JSON"}`

### 3. Полное прослушивание трека

Увеличивает вес рёбер, связанных с этим треком, на **+0.5**.

-   **URL**: `/api/recommendations/listen`
-   **Method**: `POST`
-   **Body**: `{"trackId": "ID трека из JSON"}`

### 4. Получение рекомендаций

Возвращает список из 10 рекомендованных треков.

-   **URL**: `/api/recommendations`
-   **Method**: `GET`
-   **Success Response (200 OK)**:
    ```json
    {
      "tracks": [
        {
          "id": "3NW1YMA8kfNVTzGJCGBS8m",
          "name": "Both Sides Now",
          "artists": ["Joni Mitchell"],
          "genres": ["folk", "folk rock"],
          "album": "Clouds",
          "popularity": 63
        }
      ]
    }
    ```

## Как запустить и протестировать

1.  **Убедитесь, что файл `spotify_1000_tracks_20250618_153243.json` находится в папке `src/main/resources`.**
2.  **Сборка проекта**:
    ```bash
    ./gradlew build
    ```
3.  **Запуск приложения**:
    ```bash
    java -jar build/libs/vibe-0.0.1-SNAPSHOT.jar
    ```

### Пример сценария тестирования

Для тестирования вам понадобятся `trackId` из файла `spotify_1000_tracks...json`.

1.  **Найдите в JSON-файле несколько треков одного исполнителя.** Например, у `Joni Mitchell` есть треки с ID `3NW1YMA8kfNVTzGJCGBS8m` и `0SFT0he3qL4y1Yxdp2gA4c`.

2.  **Лайкнем эти треки, чтобы сформировать предпочтение.**
    ```bash
    curl -X POST -H "Content-Type: application/json" -d '{"trackId": "3NW1YMA8kfNVTzGJCGBS8m"}' http://localhost:8080/api/recommendations/like
    curl -X POST -H "Content-Type: application/json" -d '{"trackId": "0SFT0he3qL4y1Yxdp2gA4c"}' http://localhost:8080/api/recommendations/like
    ```

3.  **Запросим рекомендации.**
    В ответе мы должны увидеть другие треки `Joni Mitchell` или треки в жанре `folk`, так как система "поняла" наш вкус.
    ```bash
    curl http://localhost:8080/api/recommendations
    ```

4.  **Поставим дизлайк одному из рекомендованных треков (возьмите ID из ответа на предыдущий запрос).**
    ```bash
    curl -X POST -H "Content-Type: application/json" -d '{"trackId": "ID_ТРЕКА_ИЗ_РЕКОМЕНДАЦИЙ"}' http://localhost:8080/api/recommendations/dislike
    ```

5.  **Снова запросим рекомендации.** Теперь дизлайкнутый трек должен исчезнуть из списка.
    ```bash
    curl http://localhost:8080/api/recommendations
    ```

## Расширение системы

-   **Добавление данных**: Просто замените или отредактируйте JSON-файл в `src/main/resources`.
-   **Изменение логики**: Основной алгоритм находится в `RecommendationService`. Логика построения графа — в `RecommendationConfig`.
-   **Настройка весов**: Значения весов для лайков/дизлайков/прослушиваний можно изменить в `LikeService`.
-   **Переход на БД**: Для production-использования `TrackGraph` можно заменить на реализацию, работающую с графовой СУБД (например, Neo4j). 