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
                        System.out.println("Âûõîä...");
                        System.exit(0);
                    default:
                        System.out.println("Ââåäåíà îïöèÿ, êîòîðîé íå ñóùåñòâóåò. Ïîïðîáóéòå ñíîâà.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Íåâåðíûé òèï ââåä¸ííûõ äàííûõ. Ïîïðîáóéòå ñíîâà.");
            } catch (Exception e) {
                System.out.println("Îøèáêà: " + e.getMessage() + ". Ïîïðîáóéòå ñíîâà.");
            }
        }
    }

    private void printMenu() {
        System.out.println("Òåêóùèé ïóòü: " + currentPath);
        System.out.println("1. Ñïèñîê ôàéëîâ è äèðåêòîðèé");
        System.out.println("2. Ñîçäàòü ôàéë");
        System.out.println("3. Ñîçäàòü äèðåêòîðèþ");
        System.out.println("4. Óäàëèòü ôàéë/äèðåêòîðèþ");
        System.out.println("5. Ñêîïèðîâàòü ôàéë");
        System.out.println("6. Ïåðåìåñòèòü ôàéë");
        System.out.println("7. Ïðîñìîòðåòü ñâîéñòâà ôàéëà");
        System.out.println("8. Èçìåíèòü òåêóùóþ äèðåêòîðèþ");
        System.out.println("9. Âûõîä");
        System.out.print("Ââåäèòå îïöèþ: ");
    }

    private void listFiles(Path directory) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            System.out.println("Ôàéëû â " + directory + ":");
            List<Path> paths = new ArrayList<>();
            stream.forEach(paths::add);
            Path[] pathsArray = paths.toArray(Path[]::new);

            Arrays.sort(pathsArray, Comparator.comparing(Path::toString)); 
            Arrays.sort(pathsArray, (p1, p2) -> Boolean.compare(Files.isDirectory(p2), Files.isDirectory(p1)));

            for (Path path : pathsArray) {
                BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                String sizeInfo = attributes.isDirectory() ? "" : " (" + formatSize(attributes.size()) + ")";
                System.out.println(path.getFileName() + sizeInfo);
            }
        } catch (IOException e) {
            System.out.println("Îøèáêà ïðè îòîáðàæåíèè ôàéëîâ: " + e.getMessage() + " Ïîïðîáóéòå ñíîâà.");
        }
    }


    private void createFile(Path path) {
        System.out.print("Ââåäèòå íàçâàíèå ôàéëà: ");
        String fileName = scanner.nextLine();
        Path filePath = path.resolve(fileName);

        try {
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                System.out.println("Ôàéë ñîçäàí â: " + filePath);
            } else {
                System.out.println("Ôàéë óæå ñóùåñòâóåò â: " + filePath);
            }
        } catch (IOException e) {
            System.out.println("Îøèáêà ïðè ñîçäàíèè ôàéëà: " + e.getMessage());
        }
    }

    private void createDirectory(Path path) {
        System.out.print("Ââåäèòå íàçâàíèå ñîçäàâàåìîé äèðåêòîðèè: ");
        String dirName = scanner.nextLine();
        Path dirPath = path.resolve(dirName);

        try {
            if (Files.notExists(dirPath)) {
                Files.createDirectory(dirPath);
                System.out.println("Äèðåêòîðèÿ ñîçäàíà â: " + dirPath);
            } else {
                System.out.println("Äèðåêòîðèÿ óæå ñóùåñòâóåò â: " + dirPath);
            }
        } catch (IOException e) {
            System.out.println("Îøèáêà ïðè ñîçäàíèè äèðåêòîðèè: " + e.getMessage());
        }
    }

    private void deleteFile(Path path) {
        System.out.print("Ââåäèòå íàçâàíèå ôàéëà/äèðåêòîðèè äëÿ å¸ óäàëåíèÿ: ");
        String fileName = scanner.nextLine();
        Path filePath = path.resolve(fileName);

        try {
            if (Files.exists(filePath)) {
                if (Files.isDirectory(filePath)) {
                    Files.walk(filePath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                } else {
                    Files.delete(filePath);
                }
                System.out.println("Ôàéë/äèðåêòîðèÿ óñïåøíî óäàëåíû: " + filePath);
            } else {
                System.out.println("Ôàéë/äèðåêòîðèÿ íå íàéäåíà: " + filePath);
            }
        } catch (IOException e) {
            System.out.println("Îøèáêà ïðè óäàëåíèè ôàéëà/äèðåêòîðèè: " + e.getMessage());
        }
    }

    private void copyFile(Path path) {
        System.out.print("Ââåäèòå íàçâàíèå ôàéëà, êîòîðûé âû áû õîòåëè ñêîïèðîâàòü: ");
        String sourceFileName = scanner.nextLine();
        System.out.print("Ââåäèòå íàçâàíèå ôàéëà, êîòîðîå áóäåò ó ñêîïèðîâàííîãî ôàéëà: ");
        String destFileName = scanner.nextLine();

        Path sourcePath = path.resolve(sourceFileName);
        Path destPath = path.resolve(destFileName);

        try {
            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Ôàéë ñêîïèðîâàí èç " + sourcePath + " â " + destPath);
        } catch (IOException e) {
            System.out.println("Îøèáêà ïðè êîïèðîâàíèè ôàéëà " + e.getMessage());
        }
    }

    private void moveFile(Path path) {
        System.out.print("Ââåäèòå íàçâàíèå ôàéëà, êîòîðûé âû áû õîòåëè ïåðåìåñòèòü: ");
        String sourceFileName = scanner.nextLine();
        System.out.print("Ââåäèòå äèðåêòîðèþ, â êîòîðóþ áóäåò ñêîïèðîâàí ôàéë: ");
        String destDirectoryName = scanner.nextLine();

        Path sourcePath = path.resolve(sourceFileName);
        Path destDirectoryPath = path.resolve(destDirectoryName);
        Path destPath = destDirectoryPath.resolve(sourceFileName);

        try {
            if (Files.exists(sourcePath)) {
                if (Files.isDirectory(destDirectoryPath)) {
                    Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Ôàéë óñïåøíî ïåðåìåù¸í èç " + sourcePath + " â " + destPath);
                } else {
                    System.out.println("Äàííîé äèðåêòîðèè íå ñóùåñòâóåò: " + destDirectoryPath);
                }
            } else {
                System.out.println("Ôàéë íå íàéäåí: " + sourcePath);
            }
        } catch (IOException e) {
            System.out.println("Îøèáêà ïðè ïåðåìåùåíèè ôàéëà: " + e.getMessage());
        }
    }

    private void fileProperties() {
        System.out.print("Ââåäèòå íàçâàíèå ôàéëà, ñâîéñòâà êîòîðîãî âû áû õîòåëè ïðîñìîòðåòü (ââåäèòå 0 äëÿ îòìåíû): ");
        String fileName = scanner.nextLine();

        if (!fileName.equals("0")) {
            Path filePath = currentPath.resolve(fileName);
            displayProperties(filePath);
        } else {
            System.out.println("Âûõîä èç ñâîéñòâ...");
        }
    }

    private void displayProperties(Path path) {
        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);

            System.out.println("Ñâîéñòâà ôàéëà:");
            System.out.println("Èìÿ: " + path.getFileName());
            System.out.println("Ïóòü: " + path);
            System.out.println("Ðàçìåð: " + formatSize(attributes.size()));
            System.out.println("Äàòà ñîçäàíèÿ: " + formatDate(attributes.creationTime(), dateFormat));
            System.out.println("Äàòà ïîñëåäíåãî èçìåíåíèÿ: " + formatDate(attributes.lastModifiedTime(), dateFormat));
            System.out.println("ßâëÿåòñÿ ëè äèðåêòîðèåé?: " + formatBoolean(attributes.isDirectory()));
            System.out.println("ßâëÿåòñÿ îáû÷íûì ôàéëîì?: " + formatBoolean(attributes.isRegularFile()));
            System.out.println("ßâëÿåòñÿ ñèìâîëè÷åñêîé ññûëêîé?: " + formatBoolean(attributes.isSymbolicLink()));
        } catch (IOException e) {
            System.out.println("Îøèáêà ïðè ÷òåíèè ñâîéñòâ ôàéëà: " + e.getMessage());
        }
    }

    private String formatSize(long sizeInBytes) {
        String[] units = {"Á", "ÊÁ", "ÌÁ", "ÃÁ", "ÒÁ", "ÏÁ"};

        int unitIndex = 0;
        double size = sizeInBytes;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", size, units[unitIndex]);
    }

    private String formatBoolean(boolean value) {
        return value ? "Äà" : "Íåò";
    }

    private String formatDate(FileTime fileTime, SimpleDateFormat dateFormat) {
        Date date = new Date(fileTime.toMillis());
        return dateFormat.format(date);
    }

    private void changeDirectory() {
        System.out.print("Ââåäèòå íàçâàíèå ïóòü èëè íàçâàíèå äèðåêòîðèè, â êîòîðóþ âû õîòèòå ïîïàñòü ('..' - íàçàä, '/' - êîðíåâàÿ äèðåêòîðèÿ): ");
        String input = scanner.nextLine();

        if (input.equals("..")) {
            goUpOneLevel();
        } else if (input.equals("/")) {
            goToRoot();
        } else {
            Path newDirPath = currentPath.resolve(input);

            if (Files.isDirectory(newDirPath)) {
                currentPath = newDirPath;
                System.out.println("Òåïåðü âû íàõîäèòåñü â: " + currentPath);
            } else {
                System.out.println("Îøèáêà: íåäåéñòâèòåëüíûé ïóòü èëè íåñóùåñòâóþùàÿ äèðåêòîðèÿ: " + newDirPath);
            }
        }
    }

    private void goUpOneLevel() {
        Path parentPath = currentPath.getParent();
        if (parentPath != null) {
            currentPath = parentPath;
            System.out.println("Òåïåðü âû íàõîäèòåñü â: " + currentPath);
        } else {
            System.out.println("Âû óæå íàõîäèòåñü â êîðíåâîé äèðåêòîðèè.");
        }
    }

    private void goToRoot() {
        currentPath = currentPath.getRoot();
        System.out.println("Òåïåðü âû â êîðíåâîé äèðåêòîðèè: " + currentPath);
    }

    public static void main(String[] args) {
        FileManager fileManager = new FileManager();
        fileManager.run();
    }
}
