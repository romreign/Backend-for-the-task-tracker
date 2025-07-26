package main.java.util.File;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CsvFileWriter {
    public static boolean writeFileContestsOrNull(Path path, String line, boolean append) {
        try {
            if (append)
                Files.writeString(path, line, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            else
                Files.writeString(path, line, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        }
        catch (IOException e) {
            System.out.println("Невозможно записать файл. Возможно файл не находится в нужной директории.");
            return false;
        }
    }
}
