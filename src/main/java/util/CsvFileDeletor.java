package main.java.util;

import java.nio.file.Files;
import java.nio.file.Path;

public class CsvFileDeletor {
    public static void deleteFile(Path path) {
        try {
            Files.delete(path);
            System.out.println("Файл успешно удалён.");
        } catch (java.io.IOException e) {
            System.err.println("Ошибка при удалении файла: " + e.getMessage());
        }
    }
}
