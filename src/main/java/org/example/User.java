package org.example;

public class User {
    private String username; // Имя пользователя
    private String userId; // UUID (ID) пользователя

    // Конструктор для инициализации имени пользователя и его UUID (ID)
    public User(String username, String userId) {
        this.username = username;
        this.userId = userId;
    }

    // Метод для получения имени пользователя
    public String getUsername() {
        return username;
    }

    // Метод для получения уникального идентификатора пользователя
    public String getUserId() {
        return userId;
    }

    // Переопределение метода toString для удобного отображения информации о пользователе
    @Override
    public String toString() {
        return "User {" +
                "username='" + username + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}