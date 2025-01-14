package org.example;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Link {
    private String originalUrl; // Оригинальный URL (оригинальная ссылка)
    private String shortUrl; // Сокращенная ссылка
    private int clickCount; // Текущее количество переходов
    private long expirationTime; // Время истечения действия ссылки
    private String userId; // ID пользователя, создавшего ссылку
    private int limit; // Лимит переходов, заданный пользователем
    private boolean limitExceeded; // Превышение лимита переходов по ссылке

    // Конструктор по умолчанию, необходим для десериализации
    public Link() {
    }

    // Конструктор с параметрами для создания объекта Link
    @JsonCreator
    public Link(@JsonProperty("originalUrl") String originalUrl,
                @JsonProperty("shortUrl") String shortUrl,
                @JsonProperty("userId") String userId,
                @JsonProperty("expirationTime") long expirationTime,
                @JsonProperty("limit") int limit,
                @JsonProperty("limitExceeded") boolean limitExceeded,
                @JsonProperty("clickCount") int clickCount) { // Добавлено поле clickCount
        this.originalUrl = originalUrl; // Инициализация оригинальной ссылки
        this.shortUrl = shortUrl; // Инициализация короткой ссылки
        this.userId = userId; // Инициализация ID пользователя
        this.expirationTime = expirationTime; // Инициализация времени истечения
        this.clickCount = clickCount; // Инициализация количества кликов
        this.limit = limit; // Инициализация лимита переходов
        this.limitExceeded = isLimitExceeded(); // Проверка на превышение лимита
    }

    // Дополнительный конструктор для создания ссылки без указания лимита
    public Link(String userIdForLink, String shortUrl, String originalUrl) {
        this.userId = userIdForLink;
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
        this.clickCount = 0; // Инициализация кликов
        this.limit = Integer.MAX_VALUE; // Установка лимита по умолчанию (по умолчанию нет ограничения на количество кликов), задает максимальное количество кликов по ссылке
        this.limitExceeded = isLimitExceeded(); // Инициализация limitExceeded
    }

    // Геттеры для получения значений полей
    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public int getClickCount() {
        return clickCount;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public String getUserId() {
        return userId;
    }

    public int getLimit() {
        return limit;
    }

    // Метод для увеличения количества кликов
    public void incrementClick() {
        this.clickCount++; // Увеличиваем количество кликов
        this.limitExceeded = isLimitExceeded(); // Обновляем статус превышения лимита
    }

    // Метод для установки нового значения количества кликов
    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
        this.limitExceeded = isLimitExceeded(); // Обновляем статус превышения лимита
    }

    // Метод для проверки превышения лимита кликов
    public boolean isLimitExceeded() {
        return clickCount >= limit;
    }

    // Метод для проверки истечения срока действия ссылки
    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }

    @Override
    public String toString() {
        return "Link{" +
                "originalUrl='" + originalUrl + '\'' +
                ", shortUrl='" + shortUrl + '\'' +
                ", clickCount=" + clickCount + // Отображение поля
                ", limit=" + limit +
                ", expirationTime=" + expirationTime +
                ", userId='" + userId + '\'' +
                ", limitExceeded=" + limitExceeded +
                '}';
    }
}