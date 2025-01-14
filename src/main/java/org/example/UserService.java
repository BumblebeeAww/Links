package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Scanner;

public class UserService {
    private static UserService instance; // Поле для хранения единственного экземпляра
    private List<User> users; // Список пользователей
    private List<ShortLink> shortLinks; // Список сокращенных ссылок
    private User currentUser ; // Текущий пользователь
    private Scanner scanner;
    private static final String USERS_FILE = "users.json"; // Файл для хранения пользователей
    private DataBase dataBase; // Объект для работы с базой данных

    // Приватный конструктор для предотвращения создания экземпляров извне
    private UserService() {
        users = new ArrayList<>(); // Инициализация списка пользователей
        shortLinks = new ArrayList<>(); // Инициализация списка сокращенных ссылок
        scanner = new Scanner(System.in);
        dataBase = new DataBase(); // Инициализация базы данных

        // Загружаем пользователей из файла при старте
        loadUsers();
    }

    // Метод для получения единственного экземпляра UserService
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService(); // Создаем новый экземпляр, если он еще не существует
        }
        return instance;
    }

    // Регистрация пользователя с созданием ID (его UUID)
    public void registerUser () {
        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine();
        String userId = UUID.randomUUID().toString(); // Генерация уникального ID

        // Проверка на уникальность пользователя
        for (User  user : users) {
            if (user.getUsername().equals(username)) {
                System.out.println("Пользователь с таким именем уже существует.");
                return;
            }
        }

        users.add(new User(username, userId)); // Добавление нового пользователя с уникальным ID
        System.out.println("Пользователь зарегистрирован: " + username);
        System.out.println("Ваш ID для входа: " + userId); // Выводим ID для пользователя

        // Сохраняем пользователей в файл
        saveUsers();
    }

    // Проверка, существует ли пользователь с данным ID
    public boolean userExists(String userId) {
        for (User  user : users) {
            if (user.getUserId().equals(userId)) {
                return true; // Пользователь найден
            }
        }
        return false; // Пользователь не найден
    }

    // Вход пользователя
    public boolean login(String userId) {
        if (!userExists(userId)) {
            return false; // Если пользователь не существует, возвращаем false
        }

        // Если пользователь существует, устанавливаем текущего пользователя
        for (User  user : users) {
            if (user.getUserId().equals(userId)) {
                currentUser  = user;
                return true; // Успешный вход
            }
        }
        return false;
    }

    // Выход пользователя
    public void logout() {
        currentUser = null;
    }

    // Получение ID текущего пользователя
    public String getUserId() {
        return currentUser  != null ? currentUser .getUserId() : null; // Возвращаем ID текущего пользователя или null
    }

    // Метод для загрузки пользователей из файла
    public void loadUsers() {
        try (Reader reader = new FileReader(USERS_FILE)) {
            Type userListType = new TypeToken<ArrayList<User>>(){}.getType(); // Определяем тип списка пользователей
            users = new Gson().fromJson(reader, userListType); // Загружаем пользователей из JSON-файла
            System.out.println("Пользователи загружены из файла.");
        } catch (FileNotFoundException e) {
            System.out.println("Файл пользователей не найден. Будет создан новый.");
        } catch (IOException e) {
            e.printStackTrace(); // Вывод ошибок, если они есть
        }
    }

    // Метод для сохранения пользователей в файл
    public void saveUsers() {
        try (Writer writer = new FileWriter(USERS_FILE)) {
            new Gson().toJson(users, writer);
            System.out.println("Пользователи сохранены в файл.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        saveUsers(); // Сохраняем пользователей перед закрытием
        if (scanner != null) {
            scanner.close(); // Закрываем сканер
        }
    }
}