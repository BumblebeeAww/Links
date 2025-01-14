package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private Properties properties; // Объект для хранения свойств из файла

    // Конструктор класса, принимающий путь к файлу
    public Config(String filePath) {
        properties = new Properties(); // Инициализация объекта Properties
        try (FileInputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для получения времени истечения ссылки
    public long getLinkExpirationTime() {
        return Long.parseLong(properties.getProperty("link.expiration.time"));
    }
}