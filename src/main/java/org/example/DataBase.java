package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataBase {
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String USERS_FILE = "users.json";
    private static final String LINKS_FILE = "links.json";

    // Метод для загрузки пользователей из JSON
    public List<User> loadUsers() throws IOException {
        return objectMapper.readValue(new File("users.json"), objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
    }

    // Метод для сохранения пользователей в JSON
    public void saveUsers(List<User> users) throws IOException {
        objectMapper.writeValue(new File("users.json"), users);
    }

    // Методы для работы с ShortLink
    public List<ShortLink> loadLinks() throws IOException {
        return objectMapper.readValue(new File("links.json"), objectMapper.getTypeFactory().constructCollectionType(List.class, ShortLink.class));
    }

    public void saveLinks(List<ShortLink> links) throws IOException {
        objectMapper.writeValue(new File("links.json"), links);
    }

    // Метод для загрузки всех данных (пользователей и ссылок)
    public void loadAll(List<User> users, List<ShortLink> links) {
        try {
            users.addAll(loadUsers());
            links.addAll(loadLinks());
            System.out.println("Данные загружены из файлов.");
        } catch (IOException e) {
            System.out.println("Не удалось загрузить данные: " + e.getMessage());
        }
    }

    // Метод для сохранения всех данных (пользователей и ссылок)
    public void saveAll(List<User> users, List<ShortLink> links) {
        try {
            saveUsers(users);
            saveLinks(links);
            System.out.println("Данные сохранены в файлы.");
        } catch (IOException e) {
            System.out.println("Не удалось сохранить данные: " + e.getMessage());
        }
    }
}