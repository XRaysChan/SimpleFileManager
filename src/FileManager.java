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
                        System.out.println("�����...");
                        System.exit(0);
                    default:
                        System.out.println("������� �����, ������� �� ����������. ���������� �����.");
                }
            } catch (NumberFormatException e) {
                System.out.println("�������� ��� �������� ������. ���������� �����.");
            } catch (Exception e) {
                System.out.println("������: " + e.getMessage() + ". ���������� �����.");
            }
        }
    }

    private void printMenu() {
        System.out.println("������� ����: " + currentPath);
        System.out.println("1. ������ ������ � ����������");
        System.out.println("2. ������� ����");
        System.out.println("3. ������� ����������");
        System.out.println("4. ������� ����/����������");
        System.out.println("5. ����������� ����");
        System.out.println("6. ����������� ����");
        System.out.println("7. ����������� �������� �����");
        System.out.println("8. �������� ������� ����������");
        System.out.println("9. �����");
        System.out.print("������� �����: ");
    }

    private void listFiles(Path directory) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            System.out.println("����� � " + directory + ":");
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
            System.out.println("������ ��� ����������� ������: " + e.getMessage() + " ���������� �����.");
        }
    }


    private void createFile(Path path) {
        System.out.print("������� �������� �����: ");
        String fileName = scanner.nextLine();
        Path filePath = path.resolve(fileName);

        try {
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                System.out.println("���� ������ �: " + filePath);
            } else {
                System.out.println("���� ��� ���������� �: " + filePath);
            }
        } catch (IOException e) {
            System.out.println("������ ��� �������� �����: " + e.getMessage());
        }
    }

    private void createDirectory(Path path) {
        System.out.print("������� �������� ����������� ����������: ");
        String dirName = scanner.nextLine();
        Path dirPath = path.resolve(dirName);

        try {
            if (Files.notExists(dirPath)) {
                Files.createDirectory(dirPath);
                System.out.println("���������� ������� �: " + dirPath);
            } else {
                System.out.println("���������� ��� ���������� �: " + dirPath);
            }
        } catch (IOException e) {
            System.out.println("������ ��� �������� ����������: " + e.getMessage());
        }
    }

    private void deleteFile(Path path) {
        System.out.print("������� �������� �����/���������� ��� � ��������: ");
        String fileName = scanner.nextLine();
        Path filePath = path.resolve(fileName);

        try {
            if (Files.exists(filePath)) {
                if (Files.isDirectory(filePath)) {
                    Files.walk(filePath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                } else {
                    Files.delete(filePath);
                }
                System.out.println("����/���������� ������� �������: " + filePath);
            } else {
                System.out.println("����/���������� �� �������: " + filePath);
            }
        } catch (IOException e) {
            System.out.println("������ ��� �������� �����/����������: " + e.getMessage());
        }
    }

    private void copyFile(Path path) {
        System.out.print("������� �������� �����, ������� �� �� ������ �����������: ");
        String sourceFileName = scanner.nextLine();
        System.out.print("������� �������� �����, ������� ����� � �������������� �����: ");
        String destFileName = scanner.nextLine();

        Path sourcePath = path.resolve(sourceFileName);
        Path destPath = path.resolve(destFileName);

        try {
            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("���� ���������� �� " + sourcePath + " � " + destPath);
        } catch (IOException e) {
            System.out.println("������ ��� ����������� ����� " + e.getMessage());
        }
    }

    private void moveFile(Path path) {
        System.out.print("������� �������� �����, ������� �� �� ������ �����������: ");
        String sourceFileName = scanner.nextLine();
        System.out.print("������� ����������, � ������� ����� ���������� ����: ");
        String destDirectoryName = scanner.nextLine();

        Path sourcePath = path.resolve(sourceFileName);
        Path destDirectoryPath = path.resolve(destDirectoryName);
        Path destPath = destDirectoryPath.resolve(sourceFileName);

        try {
            if (Files.exists(sourcePath)) {
                if (Files.isDirectory(destDirectoryPath)) {
                    Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("���� ������� ��������� �� " + sourcePath + " � " + destPath);
                } else {
                    System.out.println("������ ���������� �� ����������: " + destDirectoryPath);
                }
            } else {
                System.out.println("���� �� ������: " + sourcePath);
            }
        } catch (IOException e) {
            System.out.println("������ ��� ����������� �����: " + e.getMessage());
        }
    }

    private void fileProperties() {
        System.out.print("������� �������� �����, �������� �������� �� �� ������ ����������� (������� 0 ��� ������): ");
        String fileName = scanner.nextLine();

        if (!fileName.equals("0")) {
            Path filePath = currentPath.resolve(fileName);
            displayProperties(filePath);
        } else {
            System.out.println("����� �� �������...");
        }
    }

    private void displayProperties(Path path) {
        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);

            System.out.println("�������� �����:");
            System.out.println("���: " + path.getFileName());
            System.out.println("����: " + path);
            System.out.println("������: " + formatSize(attributes.size()));
            System.out.println("���� ��������: " + formatDate(attributes.creationTime(), dateFormat));
            System.out.println("���� ���������� ���������: " + formatDate(attributes.lastModifiedTime(), dateFormat));
            System.out.println("�������� �� �����������?: " + formatBoolean(attributes.isDirectory()));
            System.out.println("�������� ������� ������?: " + formatBoolean(attributes.isRegularFile()));
            System.out.println("�������� ������������� �������?: " + formatBoolean(attributes.isSymbolicLink()));
        } catch (IOException e) {
            System.out.println("������ ��� ������ ������� �����: " + e.getMessage());
        }
    }

    private String formatSize(long sizeInBytes) {
        String[] units = {"�", "��", "��", "��", "��", "��"};

        int unitIndex = 0;
        double size = sizeInBytes;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", size, units[unitIndex]);
    }

    private String formatBoolean(boolean value) {
        return value ? "��" : "���";
    }

    private String formatDate(FileTime fileTime, SimpleDateFormat dateFormat) {
        Date date = new Date(fileTime.toMillis());
        return dateFormat.format(date);
    }

    private void changeDirectory() {
        System.out.print("������� �������� ���� ��� �������� ����������, � ������� �� ������ ������� ('..' - �����, '/' - �������� ����������): ");
        String input = scanner.nextLine();

        if (input.equals("..")) {
            goUpOneLevel();
        } else if (input.equals("/")) {
            goToRoot();
        } else {
            Path newDirPath = currentPath.resolve(input);

            if (Files.isDirectory(newDirPath)) {
                currentPath = newDirPath;
                System.out.println("������ �� ���������� �: " + currentPath);
            } else {
                System.out.println("������: ���������������� ���� ��� �������������� ����������: " + newDirPath);
            }
        }
    }

    private void goUpOneLevel() {
        Path parentPath = currentPath.getParent();
        if (parentPath != null) {
            currentPath = parentPath;
            System.out.println("������ �� ���������� �: " + currentPath);
        } else {
            System.out.println("�� ��� ���������� � �������� ����������.");
        }
    }

    private void goToRoot() {
        currentPath = currentPath.getRoot();
        System.out.println("������ �� � �������� ����������: " + currentPath);
    }

    public static void main(String[] args) {
        FileManager fileManager = new FileManager();
        fileManager.run();
    }
}