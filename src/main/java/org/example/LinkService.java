package org.example;

import java.util.Iterator;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Scanner;

public class LinkService {
    private LinkDatabase linkDatabase; // База данных для хранения ссылок
    private UserService userService; // Сервис для управления пользователями
    private static final String BASE_URL = "http://clck.ru/"; // Базовый URL для коротких ссылок
    private Map<String, String> urlMap; // Сопоставление коротких и длинных URL
    private long linkExpirationTime; // Время жизни ссылки

    // Конструктор для инициализации сервиса ссылок
    public LinkService(UserService userService, long linkExpirationTime, LinkDatabase linkDatabase) {
        this.userService = userService;
        this.linkDatabase = new LinkDatabase();
        this.urlMap = new HashMap<>();
        this.linkExpirationTime = linkExpirationTime; // Устанавливаем время жизни ссылки
    }

    // Метод для генерации короткой ссылки
    public String generateShortLink(String originalUrl, int limit) {
        String userId = userService.getUserId();
        if (userId == null) {
            throw new IllegalStateException("Пользователь не авторизован. Пожалуйста, введите Ваш ID.");
        }

        // Проверка на существование оригинальной ссылки
        if (urlMap.containsValue(originalUrl)) {
            System.out.println("Ссылка уже существует.");
            return urlMap.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(originalUrl))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);
        }

        // Создание короткой ссылки
        String shortUrl = createShortUrl(originalUrl);
        long expirationTime = System.currentTimeMillis() + linkExpirationTime; // Устанавливаем время истечения
        boolean limitExceeded = false;
        int clickCount = 0;
        Link link = new Link(originalUrl, shortUrl, userId, expirationTime, limit, limitExceeded, clickCount);
        linkDatabase.addLink(link); // Добавление ссылки в базу данных
        return shortUrl;
    }

    // Метод для создания короткого URL
    private String createShortUrl(String originalUrl) {
        String shortId = generateShortId();
        urlMap.put(shortId, originalUrl);
        return BASE_URL + shortId;
    }

    // Метод для генерации уникального идентификатора
    private String generateShortId() {
        // Используем UUID для генерации уникального идентификатора
        return Base64.getUrlEncoder().encodeToString(UUID.randomUUID().toString().getBytes()).substring(0, 6);
    }

    // Метод для получения оригинального URL по короткой ссылке
    public String getOriginalUrl(String shortUrl) {
        String shortId = shortUrl.replace(BASE_URL, "");
        Link link = linkDatabase.getAllLinks().stream()
                .filter(l -> l.getShortUrl().equals(shortUrl))
                .findFirst()
                .orElse(null); // Поиск ссылки в базе данных

        // Проверяем условия: истекла ли ссылка или превышен ли лимит переходов
        if (link != null) {
            if (link.isExpired()) {
                linkDatabase.removeLink(shortUrl); // Удаляем ссылку из базы данных
                urlMap.remove(shortId); // Удаляем из urlMap
                System.out.println("Ссылка " + shortUrl + " истекла и была удалена.");
                return null; // Ссылка истекла
            } else if (link.isLimitExceeded()) {
                linkDatabase.removeLink(shortUrl); // Удаляем ссылку из базы данных
                urlMap.remove(shortId); // Удаляем из urlMap
                System.out.println("Лимит переходов по ссылке " + shortUrl + " превышен и она была удалена.");
                return null; // Лимит превышен
            } else {
                link.incrementClick(); // Увеличиваем счетчик переходов
                return link.getOriginalUrl(); // Возвращаем оригинальный URL
            }
        }
        System.out.println("Ссылка " + shortUrl + " не найдена.");
        return null;
    }

    // Метод для редактирования пользователем количества переходов по ссылке
    public void editLinkClicks(String shortUrl, int newClickCount) {
        Link link = linkDatabase.getAllLinks().stream()
                .filter(l -> l.getShortUrl().equals(shortUrl))
                .findFirst()
                .orElse(null);

        if (link != null) {
            link.setClickCount(newClickCount); // Пользователь устанавливает новое количество кликов
            System.out.println("Количество переходов по ссылке " + shortUrl + " изменено на " + newClickCount);
        } else {
            System.out.println("Ссылка " + shortUrl + " не найдена.");
        }
    }
}