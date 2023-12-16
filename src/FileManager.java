import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileManager {
    private final Scanner scanner;
    private Path currentPath;

    public FileManager() {
        this.scanner = new Scanner(System.in);
        this.currentPath = Paths.get(System.getProperty("user.dir"));
    }

    public void run() {
        while (true) {
            printMenu();
            try {
                String input = scanner.nextLine();
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1:
                        listFiles(currentPath);
                        break;
                    case 2:
                        createFile(currentPath);
                        break;
                    case 3:
                        createDirectory(currentPath);
                        break;
                    case 4:
                        deleteFile(currentPath);
                        break;
                    case 5:
                        copyFile(currentPath);
                        break;
                    case 6:
                        moveFile(currentPath);
                        break;
                    case 7:
                        fileProperties();
                        break;
                    case 8:
                        changeDirectory();
                        break;
                    case 9:
                        System.out.println("Выход...");
                        System.exit(0);
                    default:
                        System.out.println("Введена опция, которой не существует. Попробуйте снова.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Неверный тип введённых данных. Попробуйте снова.");
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
            }
        }
    }

    private void printMenu() {
        System.out.println("Текущий путь: " + currentPath);
        System.out.println("1. Список файлов и директорий");
        System.out.println("2. Создать файл");
        System.out.println("3. Создать директорию");
        System.out.println("4. Удалить файл/директорию");
        System.out.println("5. Скопировать файл");
        System.out.println("6. Переместить файл");
        System.out.println("7. Просмотреть свойства файла");
        System.out.println("8. Изменить текущую директорию");
        System.out.println("9. Выход");
        System.out.print("Введите опцию: ");
    }

    private void listFiles(Path directory) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            System.out.println("Files in " + directory + ":");
            List<Path> paths = new ArrayList<>();
            stream.forEach(paths::add); // Convert stream to list
            Path[] pathsArray = paths.toArray(Path[]::new);

            Arrays.sort(pathsArray, Comparator.comparing(Path::toString)); // Sort paths alphabetically
            Arrays.sort(pathsArray, (p1, p2) -> Boolean.compare(Files.isDirectory(p2), Files.isDirectory(p1))); // Sort by directory first

            for (Path path : pathsArray) {
                BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                String sizeInfo = attributes.isDirectory() ? "" : " (" + formatSize(attributes.size()) + ")";
                System.out.println(path.getFileName() + sizeInfo);
            }
        } catch (IOException e) {
            System.out.println("Error listing files: " + e.getMessage());
        }
    }


    private void createFile(Path path) {
        System.out.print("Введите название файла: ");
        String fileName = scanner.nextLine();
        Path filePath = path.resolve(fileName);

        try {
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                System.out.println("Файл создан в: " + filePath);
            } else {
                System.out.println("Файл уже существует в: " + filePath);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при создании файла: " + e.getMessage());
        }
    }

    private void createDirectory(Path path) {
        System.out.print("Введите название создаваемой директории: ");
        String dirName = scanner.nextLine();
        Path dirPath = path.resolve(dirName);

        try {
            if (Files.notExists(dirPath)) {
                Files.createDirectory(dirPath);
                System.out.println("Директория создана в: " + dirPath);
            } else {
                System.out.println("Директория уже существует в: " + dirPath);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при создании директории: " + e.getMessage());
        }
    }

    private void deleteFile(Path path) {
        System.out.print("Введите название файла/директории для её удаления: ");
        String fileName = scanner.nextLine();
        Path filePath = path.resolve(fileName);

        try {
            if (Files.exists(filePath)) {
                if (Files.isDirectory(filePath)) {
                    Files.walk(filePath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                } else {
                    Files.delete(filePath);
                }
                System.out.println("Файл/директория успешно удалены: " + filePath);
            } else {
                System.out.println("Файл/директория не найдена: " + filePath);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при удалении файла/директории: " + e.getMessage());
        }
    }

    private void copyFile(Path path) {
        System.out.print("Введите название файла, который вы бы хотели скопировать: ");
        String sourceFileName = scanner.nextLine();
        System.out.print("Введите название файла, которое будет у скопированного файла: ");
        String destFileName = scanner.nextLine();

        Path sourcePath = path.resolve(sourceFileName);
        Path destPath = path.resolve(destFileName);

        try {
            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Файл скопирован из " + sourcePath + " в " + destPath);
        } catch (IOException e) {
            System.out.println("Ошибка при копировании файла " + e.getMessage());
        }
    }

    private void moveFile(Path path) {
        System.out.print("Введите название файла, который вы бы хотели переместить: ");
        String sourceFileName = scanner.nextLine();
        System.out.print("Введите директорию, в которую будет скопирован файл: ");
        String destDirectoryName = scanner.nextLine();

        Path sourcePath = path.resolve(sourceFileName);
        Path destDirectoryPath = path.resolve(destDirectoryName);
        Path destPath = destDirectoryPath.resolve(sourceFileName);

        try {
            if (Files.exists(sourcePath)) {
                if (Files.isDirectory(destDirectoryPath)) {
                    Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Файл успешно перемещён из " + sourcePath + " в " + destPath);
                } else {
                    System.out.println("Данной директории не существует: " + destDirectoryPath);
                }
            } else {
                System.out.println("Файл не найден: " + sourcePath);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при перемещении файла: " + e.getMessage());
        }
    }

    private void fileProperties() {
        System.out.print("Введите название файла, свойства которого вы бы хотели просмотреть (введите 0 для отмены): ");
        String fileName = scanner.nextLine();

        if (!fileName.equals("0")) {
            Path filePath = currentPath.resolve(fileName);
            displayProperties(filePath);
        } else {
            System.out.println("Exiting file properties.");
        }
    }

    private void displayProperties(Path path) {
        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);

            System.out.println("Свойства файла:");
            System.out.println("Имя: " + path.getFileName());
            System.out.println("Путь: " + path);
            System.out.println("Размер: " + formatSize(attributes.size()));
            System.out.println("Дата создания: " + formatDate(attributes.creationTime(), dateFormat));
            System.out.println("Дата последнего изменения: " + formatDate(attributes.lastModifiedTime(), dateFormat));
            System.out.println("Является ли директорией?: " + formatBoolean(attributes.isDirectory()));
            System.out.println("Является обычным файлом?: " + formatBoolean(attributes.isRegularFile()));
            System.out.println("Является символической ссылкой?: " + formatBoolean(attributes.isSymbolicLink()));
        } catch (IOException e) {
            System.out.println("Ошибка при чтении свойств файла: " + e.getMessage());
        }
    }

    private String formatSize(long sizeInBytes) {
        String[] units = {"Б", "КБ", "МБ", "ГБ", "ТБ", "ПБ"};

        int unitIndex = 0;
        double size = sizeInBytes;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", size, units[unitIndex]);
    }

    private String formatBoolean(boolean value) {
        return value ? "Да" : "Нет";
    }

    private String formatDate(FileTime fileTime, SimpleDateFormat dateFormat) {
        Date date = new Date(fileTime.toMillis());
        return dateFormat.format(date);
    }

    private void changeDirectory() {
        System.out.print("Введите название путь или название директории, в которую вы хотите попасть ('..' - назад, '/' - корневая директория): ");
        String input = scanner.nextLine();

        if (input.equals("..")) {
            goUpOneLevel();
        } else if (input.equals("/")) {
            goToRoot();
        } else {
            Path newDirPath = currentPath.resolve(input);

            if (Files.isDirectory(newDirPath)) {
                currentPath = newDirPath;
                System.out.println("Теперь вы находитесь в: " + currentPath);
            } else {
                System.out.println("Ошибка: недействительный путь или несуществующая директория: " + newDirPath);
            }
        }
    }

    private void goUpOneLevel() {
        Path parentPath = currentPath.getParent();
        if (parentPath != null) {
            currentPath = parentPath;
            System.out.println("Теперь вы находитесь в: " + currentPath);
        } else {
            System.out.println("Вы уже находитесь в корневой директории.");
        }
    }

    private void goToRoot() {
        currentPath = currentPath.getRoot();
        System.out.println("Теперь вы в корневой директории: " + currentPath);
    }

    public static void main(String[] args) {
        FileManager fileManager = new FileManager();
        fileManager.run();
    }
}