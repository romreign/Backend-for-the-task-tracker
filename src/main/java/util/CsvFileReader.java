package main.java.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CsvFileReader {
    public static String readFileContestsOrNull(Path path) {
        try {
            return Files.readString(path);
        }
        catch (IOException e) {
            System.out.println("Невозможно прочитать файл. Возможно файл не находится в нужной директории.");
            return null;
        }
    }
}
