package org.example;

public class ShortLink {
    private String url;        // Оригинальный URL
    private String shortUrl;   // Сокращенный URL
    private String userId;     // ID пользователя, который создал ссылку

    @Override
    public String toString() {
        return "ShortLink {" +
                "url='" + url + '\'' +
                ", shortUrl='" + shortUrl + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}