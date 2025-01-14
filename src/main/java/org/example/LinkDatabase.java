package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class LinkDatabase {
    private List<Link> links; // Список для хранения ссылок
    private ObjectMapper objectMapper;
    private final String filePath = "links.json"; // Путь к файлу для сохранения/загрузки ссылок

    public LinkDatabase() {
        links = new ArrayList<>(); // Инициализация списка ссылок
        objectMapper = new ObjectMapper(); // Инициализация ObjectMapper
        loadLinks(); // Загружаем ссылки при инициализации
    }

    // Метод для добавления новой ссылки
    public void addLink(Link link) {
        links.add(link);
    }

    public List<Link> getAllLinks() {
        return links;
    }

    // Метод для удаления ссылки по короткому URL
    public void removeLink(String shortUrl) {
        Link linkToRemove = links.stream()
                .filter(link -> link.getShortUrl().equals(shortUrl))
                .findFirst()
                .orElse(null);
        // Если ссылка не найдена, выбрасываем исключение
        if (linkToRemove == null) {
            throw new NoSuchElementException("Ссылка не найдена в базе данных: " + shortUrl);
        }
        // Удаляем ссылку из коллекции
        links.remove(linkToRemove);
    }

    // Метод для загрузки ссылок из файла
    public void loadLinks() {
        try {
            File file = new File(filePath);
            if (file.exists()) { // Проверяем, существует ли файл
                Link[] loadedLinks = objectMapper.readValue(new File(filePath), Link[].class);
                for (Link link : loadedLinks) {
                    links.add(link);
                }
                System.out.println("Ссылки успешно загружены из " + filePath);
            } else {
                System.out.println("Файл не найден, загружаем пустой список ссылок.");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке ссылок: " + e.getMessage());
        }
    }

    // Метод для сохранения ссылок в файл
    public void saveLinks() {
        try {
            File file = new File(filePath);
            // Создаем файл, если он не существует
            if (!file.exists()) {
                file.createNewFile(); // Создаем новый файл
            }
            objectMapper.writeValue(new File(filePath), links);
            System.out.println("Ссылки успешно сохранены в " + filePath);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении ссылок: " + e.getMessage());
        }
    }
}