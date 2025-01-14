package org.example;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Config config = new Config("config.properties.txt"); // Чтение конфигурации из файла config.properties.txt
        long linkExpirationTime = config.getLinkExpirationTime();

        Scanner scanner = new Scanner(System.in);
        UserService userService = UserService.getInstance(); // Получаем единственный экземпляр UserService
        LinkDatabase linkDatabase = new LinkDatabase(); // Создаем экземпляр LinkDatabase
        LinkService linkService = new LinkService(userService, linkExpirationTime, linkDatabase);

        linkDatabase.loadLinks(); // Загрузка ссылок при старте программы

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {  // Добавление функции для сохранения ссылок перед выходом
            System.out.println("Сохраняем ссылки перед выходом...");
            linkDatabase.saveLinks();
            System.out.println("Ссылки сохранены.");
        }));

        String currentUserId = null;

        while (true) {
            // Меню действий для пользователя
            System.out.println("1. Регистрация пользователя");
            System.out.println("2. Войти");
            System.out.println("3. Выйти");
            System.out.println("4. Создание короткой ссылки");
            System.out.println("5. Переход по короткой ссылке");
            System.out.println("6. Редактирование количества переходов по короткой ссылке");
            System.out.println("0. Закрытие программы");
            System.out.print("Выберите действие: ");

            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Пожалуйста, введите число.");
                scanner.nextLine();
                continue;
            }

            switch (choice) {
                case 1: // Регистрация пользователя
                    userService.registerUser ();
                    break;
                case 2: // Вход пользователя
                    System.out.print("Введите Ваш ID для входа в программу: ");
                    String userId = scanner.nextLine();
                    if (userService.userExists(userId)) {
                        if (userService.login(userId)) {
                            System.out.println("Вход выполнен успешно");
                            currentUserId = userId; // Сохраняем Id текущего пользователя
                        } else {
                            System.out.println("Ошибка входа. Проверьте, пожалуйста, ваш ID.");
                        }
                    } else {
                        System.out.println("Пользователь с таким ID не найден.");
                    }
                    break;
                case 3: // Выход из сеанса
                    userService.logout();
                    System.out.println("Вы вышли из системы.");
                    currentUserId = null; // Сбрасываем текущего пользователя
                    break;
                case 4: // Создание короткой ссылки
                    if (currentUserId == null) {
                    System.out.println("Сначала войдите в систему."); // Проверка, что пользователь успешно выполнил вход
                    break;
                }
                System.out.print("Введите длинную ссылку: ");
                String originalUrl = scanner.nextLine();
                System.out.print("Введите лимит переходов: ");
                int limit;
                try {
                    limit = scanner.nextInt(); // Считываем лимит переходов, указанных пользователем
                    scanner.nextLine();
                } catch (InputMismatchException e) {
                    System.out.println("Пожалуйста, введите число для лимита переходов.");
                    scanner.nextLine();
                    continue;
                }
                // Генерация короткой ссылки
                String shortUrl = linkService.generateShortLink(originalUrl, limit);
                if (shortUrl != null) {
                    System.out.println("Короткая ссылка: " + shortUrl);
                    // Добавляем ссылку в базу данных
                    linkDatabase.addLink(new Link(currentUserId, shortUrl, originalUrl));
                }
                break;
                case 5: // Переход по короткой ссылке
                    System.out.print("Введите короткую ссылку: ");
                    String shortLink = scanner.nextLine();
                    String redirectUrl = linkService.getOriginalUrl(shortLink); // Получение оригинального URL
                    if (redirectUrl != null) {
                        System.out.println("Открываем оригинальный URL: " + redirectUrl);
                        openWebpage(redirectUrl); // Открытие URL в браузере
                    } else {
                        System.out.println("Ссылка не найдена.");
                    }
                    break;
                case 6: // Редактирование количества переходов по короткой ссылке
                    System.out.print("Введите короткую ссылку для редактирования: ");
                    String editShortUrl = scanner.nextLine();
                    System.out.print("Введите новое количество переходов: ");
                    int newClickCount;
                    try {
                        newClickCount = scanner.nextInt(); // Считываем новое количество переходов
                        scanner.nextLine();
                    } catch (InputMismatchException e) {
                        System.out.println("Пожалуйста, введите число для количества переходов.");
                        scanner.nextLine();
                        continue;
                    }
                    // Редактирование количества переходов
                    linkService.editLinkClicks(editShortUrl, newClickCount);
                    linkDatabase.saveLinks(); // Сохраняем изменения после редактирования
                    break;
                case 0: // Закрытие программы
                    System.out.println("Сохраняем ссылки перед выходом...");
                    linkDatabase.saveLinks(); // Сохраняем ссылки перед выходом
                    System.out.println("Выход из программы...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте еще раз."); // Обработка неверного выбора
            }
        }
    }

    // Метод для открытия URL в браузере
    private static void openWebpage(String urlString) {
        try {
            URI uri = new URI(urlString); // Создаем URI из строки
            Desktop desktop = Desktop.getDesktop(); // Получаем объект Desktop
            desktop.browse(uri); // Открываем URL в браузере
        } catch (IOException | URISyntaxException e) {
            System.out.println("Не удалось открыть URL: " + e.getMessage()); // Вывод сообщения в случае ошибки
        }
    }
}