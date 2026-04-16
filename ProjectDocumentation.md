# Подробная документация проекта ChecksumCalculator

## Обзор проекта

ChecksumCalculator - это Java-приложение для командной строки, предназначенное для вычисления и проверки контрольных сумм файлов и директорий. Оно обеспечивает целостность данных, обнаруживая изменения, удаления или добавления в директории.

### Основные возможности:
- Вычисление контрольных сумм для файлов и директорий
- Проверка целостности файлов по предварительно вычисленным суммам
- Обработка символических ссылок
- Отображение прогресса с визуальным индикатором
- Поддержка паузы и возобновления операций
- Генерация отчетов в различных форматах

### Архитектура:
Проект использует паттерны проектирования:
- **Strategy**: Для различных алгоритмов хеширования (MD5, SHA-256)
- **Observer**: Для отслеживания прогресса
- **Composite**: Для представления файловой структуры
- **Memento**: Для сохранения и восстановления состояния сканирования

## Структура файлов проекта

### Корневые файлы

#### ChecksumCalculator.iml
Файл конфигурации IntelliJ IDEA для проекта.

#### GUIDE.md
Руководство пользователя с описанием команд и примеров использования.

### Исходный код (src/main/java/checker/)

#### ChecksumCalculator.java
**Описание:** Интерфейс, определяющий метод для вычисления контрольных сумм.

**Разбор кода:**
```java
/**
 * Този интерфейс дефинира метода за изчисляване на контролни суми.
 * Реализира се от конкретни класове като Md5Calculator и Sha256Calculator.
 */
package checker;

import java.io.IOException;
import java.io.InputStream;

public interface ChecksumCalculator {
    String calculate(InputStream input) throws IOException;
}
```
- Определяет контракт для всех реализаций калькуляторов контрольных сумм
- Метод `calculate` принимает InputStream и возвращает строку с хешем
- Может бросать IOException при ошибках чтения

#### Md5Calculator.java
**Описание:** Реализация интерфейса ChecksumCalculator для алгоритма MD5.

**Разбор кода:**
```java
/**
 * Този клас реализира изчисляването на MD5 контролни суми.
 * Използва Java's MessageDigest за хеширане на файлове.
 */
package checker;

import java.io.InputStream;
import java.security.MessageDigest;

public class Md5Calculator implements ChecksumCalculator {
    @Override
    public String calculate(InputStream is) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[4096];

            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            byte[] hashBytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error calculating MD5", e);
        }
    }
}
```
- Использует MessageDigest с алгоритмом "MD5"
- Читает данные блоками по 4096 байт для эффективности
- Преобразует байты хеша в шестнадцатеричную строку
- Оборачивает все исключения в RuntimeException

#### Sha256Calculator.java
**Описание:** Реализация интерфейса ChecksumCalculator для алгоритма SHA-256.

**Разбор кода:**
Аналогично Md5Calculator, но использует "SHA-256" алгоритм. Более безопасный вариант MD5, рекомендуется для новых проектов.

#### ChecksumProcessor.java
**Описание:** Основной класс, управляющий процессом обработки файлов. Обходит структуру директорий, вычисляет хеши, сообщает о прогрессе и поддерживает паузу/возобновление.

**Ключевые поля:**
- `calculator`: Экземпляр ChecksumCalculator
- `reporter`: ProgressReporter для уведомлений
- `controller`: ScanController для управления паузой
- Счетчики: totalFiles, processedFiles, totalBytes, totalBytesRead
- `completedChecksums`: Карта завершенных контрольных сумм

**Основные методы:**
- `process(FileSystemNode)`: Запускает обработку
- `processNode()`: Рекурсивно обрабатывает узлы
- `processFile()`: Обрабатывает отдельный файл
- Memento паттерн: `createMemento()`, `restoreMemento()`

**Разбор кода:**
Класс использует паттерн Composite для обхода дерева файловой системы, Observer для прогресса и Memento для сохранения состояния.

#### CommandLineInterface.java
**Описание:** Класс для взаимодействия с пользователем через командную строку. Обрабатывает команды паузы, возобновления и остановки.

**Разбор кода:**
Содержит main метод, который строит файловую структуру и печатает дерево. Используется для демонстрации структуры файловой системы.

#### ChecksumCalculatorMain.java
**Описание:** Основная входная точка приложения. Обрабатывает аргументы командной строки, управляет процессом вычисления или проверки файлов и предоставляет интерактивный интерфейс.

**Ключевые компоненты:**
- Парсинг аргументов командной строки
- Создание дерева файловой системы
- Настройка системы прогресса
- Запуск фонового потока для команд пользователя
- Выбор алгоритма хеширования
- Запуск процесса вычисления или верификации

**Разбор кода:**
```java
public static void main(String[] args) {
    // Парсинг аргументов
    CommandLineOptions options = CommandLineOptions.parse(args);
    
    // Проверка на помощь
    if (options.isHelp()) {
        printUsage();
        return;
    }
    
    // Запуск основной логики
    run(options);
}
```
- Использует паттерн Command для обработки аргументов
- Создает отдельный поток для интерактивных команд
- Поддерживает режимы calculate и verify
- Интегрирует все компоненты системы

#### CommandLineOptions.java
**Описание:** Класс для обработки и валидации опций командной строки.

**Основные возможности:**
- Парсинг аргументов: режим, путь, алгоритм, файл сумм и т.д.
- Валидация режима (calculate, verify, pause, resume)
- Поддержка флагов (--help, -nfl)

**Разбор кода:**
```java
public static CommandLineOptions parse(String[] args) {
    // Значения по умолчанию
    String mode = "calculate";
    Path path = Paths.get(".");
    String algorithm = "sha256";
    
    // Парсинг аргументов
    for (int i = 0; i < arguments.size(); i++) {
        String arg = arguments.get(i);
        if (arg.startsWith("-m=")) {
            mode = arg.substring("-m=".length());
        }
        // ... другие аргументы
    }
    
    // Валидация
    if (!"calculate".equals(mode) && !"verify".equals(mode) && ...) {
        throw new IllegalArgumentException("Invalid mode: " + mode);
    }
    
    return new CommandLineOptions(...);
}
```
- Использует простой парсинг аргументов без внешних библиотек
- Предоставляет методы-предикаты для режимов
- Валидирует входные данные

#### ChecksumFileParser.java
**Описание:** Класс для парсинга файлов с контрольными суммами.

**Разбор кода:**
```java
public static Map<String,String> parse(Path checksumFile) throws IOException {
    Map<String,String> checksums = new LinkedHashMap<>();
    for (String rawLine : Files.readAllLines(checksumFile)) {
        String line = rawLine.trim();
        if (line.isEmpty() || line.startsWith("#")) {
            continue; // Пропуск пустых строк и комментариев
        }
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2) {
            continue;
        }
        String hash = parts[0].trim();
        String path = parts[1].trim();
        if (path.startsWith("*")) {
            path = path.substring(1); // Удаление маркера бинарного режима
        }
        path = path.replace('\\', '/'); // Нормализация путей
        checksums.put(path, hash);
    }
    return checksums;
}
```
- Поддерживает формат файлов контрольных сумм (как у md5sum/sha256sum)
- Игнорирует комментарии и пустые строки
- Нормализует пути для кроссплатформенности

#### ReportWriter.java
**Описание:** Класс для записи отчетов о контрольных суммах и результатах проверки.

**Разбор кода:**
Поддерживает два формата: TEXT и JSON. Предоставляет методы для печати в консоль и сохранения в файлы.

#### ScanController.java
**Описание:** Класс для управления состоянием сканирования (пауза, возобновление, остановка).

**Разбор кода:**
```java
public class ScanController {
    private volatile boolean paused;
    private volatile boolean stopRequested;
    private final Object lock = new Object();
    
    public void pause() {
        synchronized (lock) {
            paused = true;
        }
    }
    
    public void resume() {
        synchronized (lock) {
            paused = false;
            lock.notifyAll();
        }
    }
    
    public void waitIfPaused() {
        synchronized (lock) {
            while (paused) {
                try {
                    lock.wait();
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
```
- Использует synchronized для потокобезопасности
- Предоставляет механизм ожидания при паузе
- Поддерживает graceful shutdown

#### VerificationStatus.java
**Описание:** Класс, представляющий статус проверки для конкретного файла.

**Поля:**
- path: путь к файлу
- result: результат проверки (OK, MODIFIED, NEW, REMOVED)
- expectedChecksum: ожидаемая сумма
- actualChecksum: фактическая сумма

#### StateStorage.java
**Описание:** Класс для сохранения и загрузки состояния сканирования.

**Разбор кода:**
Использует Properties для сериализации состояния. Сохраняет прогресс, чтобы можно было возобновить сканирование после прерывания.

#### ProgressInputStream.java
**Описание:** Обертка вокруг InputStream для отслеживания прогресса чтения.

**Разбор кода:**
```java
public class ProgressInputStream extends FilterInputStream {
    private final LongConsumer progressListener;
    
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count = super.read(b, off, len);
        if (count > 0) {
            progressListener.accept(count);
        }
        return count;
    }
}
```
- Наследуется от FilterInputStream
- Уведомляет о прогрессе при каждом чтении
- Используется для точного отслеживания байтов

#### ReportFormat.java
**Описание:** Перечисление форматов отчетов (TEXT, JSON).

#### VerificationResult.java
**Описание:** Перечисление результатов верификации (OK, MODIFIED, NEW, REMOVED).

#### ScanMemento.java
**Описание:** Класс для хранения моментального снимка состояния сканирования (паттерн Memento).

**Поля:**
- Статистика сканирования (totalFiles, processedFiles, etc.)
- Текущее состояние (currentFile, currentFileBytes)
- Завершенные контрольные суммы

### Пакет node/

#### FileSystemNode.java
**Описание:** Абстрактный базовый класс для узлов файловой системы.

**Разбор кода:**
```java
abstract public class FileSystemNode {
    protected final String name;
    protected final Path path;
    
    public String getRelativePath(Path root) {
        if (root == null) {
            return path.toString();
        }
        try {
            return root.relativize(path).toString();
        } catch (IllegalArgumentException e) {
            return path.toString();
        }
    }
    
    public abstract long getSize();
}
```
- Определяет общий интерфейс для файлов и директорий
- Предоставляет методы для получения относительных путей
- Абстрактный метод getSize() для вычисления размера

#### FileNode.java
**Описание:** Представляет файл в дереве файловой системы.

**Разбор кода:**
```java
public class FileNode extends FileSystemNode {
    private final long size;
    
    @Override
    public long getSize() {
        return size;
    }
}
```
- Хранит размер файла
- Листовой узел в паттерне Composite

#### DirectoryNode.java
**Описание:** Представляет директорию в дереве файловой системы.

**Разбор кода:**
```java
public class DirectoryNode extends FileSystemNode {
    private final List<FileSystemNode> children = new ArrayList<>();
    
    public void addChild(FileSystemNode node) {
        children.add(node);
    }
    
    @Override
    public long getSize() {
        return children.stream()
                .mapToLong(FileSystemNode::getSize)
                .sum();
    }
}
```
- Содержит список дочерних узлов
- Размер директории = сумма размеров дочерних элементов
- Контейнер в паттерне Composite

#### FileSystemBuilder.java
**Описание:** Строит дерево файловой системы из заданного пути.

**Разбор кода:**
```java
public class FileSystemBuilder {
    private final boolean followLinks;
    private final Set<Path> visitedPaths = new HashSet<>();
    
    public FileSystemNode build(Path path) throws IOException {
        visitedPaths.clear();
        return buildRecursive(path.toAbsolutePath().normalize());
    }
    
    private FileSystemNode buildRecursive(Path path) throws IOException {
        // Проверка на циклы
        Path realPath = path.toRealPath();
        if (visitedPaths.contains(realPath)) {
            throw new FileSystemException("Цикъл открит при: " + path);
        }
        visitedPaths.add(realPath);
        
        if (Files.isDirectory(path)) {
            DirectoryNode directory = new DirectoryNode(path);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path entry : stream) {
                    directory.addChild(buildRecursive(entry));
                }
            }
            return directory;
        } else {
            return new FileNode(path, Files.size(path));
        }
    }
}
```
- Рекурсивно строит дерево файловой системы
- Обнаруживает циклы в символьных ссылках
- Поддерживает настройку followLinks
- Использует паттерн Builder

### Тесты (src/test/java/checker/)

#### Md5CalculatorTest.java
**Описание:** Модульные тесты для Md5Calculator.

**Разбор кода:**
```java
@Test
void testCalculateAbc() {
    Md5Calculator calculator = new Md5Calculator();
    String input = "abc";
    ByteArrayInputStream is = new ByteArrayInputStream(input.getBytes());

    String result = calculator.calculate(is);

    // Очакваната стойност е взета от материалите
    assertEquals("900150983cd24fb0d6963f7d28e17f72", result, "MD5 for 'abc' is incorrect");
}
```
- Тестирует корректность вычисления MD5 для строки "abc"
- Использует ByteArrayInputStream для симуляции входного потока
- Проверяет ожидаемое значение хеша

#### Sha256CalculatorTest.java
**Описание:** Модульные тесты для Sha256Calculator.

**Разбор кода:**
Аналогично Md5CalculatorTest, но для SHA-256 алгоритма.

#### ChecksumProcessorTest.java
**Описание:** Тесты для ChecksumProcessor с использованием временных файлов.

**Разбор кода:**
Создает временные файлы и директории для тестирования процессора контрольных сумм.

#### CommandLineOptionsTest.java
**Описание:** Тесты для парсинга опций командной строки.

**Разбор кода:**
Тестирует различные комбинации аргументов командной строки и их корректный парсинг.

#### ChecksumFileParserTest.java
**Описание:** Тесты для парсинга файлов с контрольными суммами.

**Разбор кода:**
Создает тестовые файлы с контрольными суммами и проверяет их корректный разбор.

#### ReportWriterTest.java
**Описание:** Тесты для записи отчетов.

**Разбор кода:**
Тестирует генерацию отчетов в текстовом и JSON форматах.

#### FileSystemBuilderTest.java
**Описание:** Тесты для построения файловой структуры.

**Разбор кода:**
```java
@Test
void testBuildHierarchyFromRealFileSystem() throws IOException {
    //Подготовка на тестова структура на диска
    // tempDir/
    //    file1.dat (10 bytes)
    //    subdir/
    //       file2.dat (20 bytes)

    Path file1 = tempDir.resolve("file1.dat");
    Files.write(file1, new byte[10]);

    Path subdir = tempDir.resolve("subdir");
    Files.createDirectory(subdir);

    Path file2 = subdir.resolve("file2.dat");
    Files.write(file2, new byte[20]);

    //Изпълнение на Builder-а
    FileSystemBuilder builder = new FileSystemBuilder(false);
    FileSystemNode root = builder.build(tempDir);

    //Проверки
    assertNotNull(root, "Коренният възел не трябва да е null");
    assertEquals(30, root.getSize(), "Общият размер на изграденото дърво трябва да е 30 байта.");
    assertEquals(tempDir.getFileName().toString(), root.getName());
}
```
- Создает временную файловую структуру
- Строит дерево с помощью FileSystemBuilder
- Проверяет корректность построенного дерева

#### FileSystemNodeTest.java
**Описание:** Тесты для узлов файловой системы.

**Разбор кода:**
Тестирует методы getSize(), getName(), getRelativePath() для FileNode и DirectoryNode.

#### ProgressReporterTest.java
**Описание:** Тесты для системы прогресса.

**Разбор кода:**
Тестирует уведомления наблюдателей о прогрессе.

#### ConsoleObserverTest.java
**Описание:** Тесты для консольного наблюдателя.

**Разбор кода:**
Проверяет вывод прогресса в консоль.

#### ScanControllerTest.java
**Описание:** Тесты для контроллера сканирования.

**Разбор кода:**
Тестирует паузу, возобновление и остановку сканирования.

#### VerificationStatusTest.java
**Описание:** Тесты для статуса верификации.

**Разбор кода:**
Тестирует различные состояния верификации файлов.

#### StateStorageTest.java
**Описание:** Тесты для хранения состояния.

**Разбор кода:**
Тестирует сохранение и загрузку состояния сканирования.

### Скомпилированные классы (target/classes/)
Содержит .class файлы, соответствующие исходному коду.

### Библиотеки (lib/)
Внешние зависимости проекта.

## Запуск и использование

### Компиляция
```bash
javac -cp . -d target/classes src/main/java/checker/*.java src/main/java/checker/node/*.java
```

### Запуск
```bash
# Вычисление контрольных сумм
java -cp target/classes checker.ChecksumCalculatorMain -m=calculate -p=/path/to/directory -a=sha256

# Проверка контрольных сумм
java -cp target/classes checker.ChecksumCalculatorMain -m=verify -p=/path/to/directory -c=checksums.txt

# Получение справки
java -cp target/classes checker.ChecksumCalculatorMain --help
```

### Примеры использования

#### Вычисление SHA-256 сумм для директории
```bash
java -cp target/classes checker.ChecksumCalculatorMain -m=calculate -p=. -a=sha256 -c=my_checksums.txt
```

#### Проверка целостности файлов
```bash
java -cp target/classes checker.ChecksumCalculatorMain -m=verify -p=. -c=my_checksums.txt -t=verification_report.txt
```

#### Интерактивные команды во время сканирования
Во время выполнения программы можно использовать команды:
- `pause` - приостановить сканирование
- `resume` - возобновить сканирование
- `quit` - завершить сканирование

## Архитектурные паттерны

Проект демонстрирует применение следующих паттернов проектирования:

1. **Strategy** - ChecksumCalculator и его реализации (Md5Calculator, Sha256Calculator)
2. **Observer** - система прогресса с ProgressReporter и наблюдателями
3. **Composite** - иерархия FileSystemNode для представления структуры файловой системы
4. **Memento** - ScanMemento для сохранения состояния сканирования
5. **Builder** - FileSystemBuilder для построения дерева файловой системы
6. **Command** - обработка аргументов командной строки

## Тестирование

Проект использует JUnit 5 для модульного тестирования. Запуск тестов:

```bash
# Компиляция с тестами
javac -cp ".:lib/*" -d target/classes src/main/java/checker/*.java src/main/java/checker/node/*.java src/test/java/checker/*.java

# Запуск тестов (пример)
java -cp "target/classes:lib/*" org.junit.platform.console.ConsoleLauncher --scan-classpath
```

## Расширение проекта

Для добавления нового алгоритма хеширования:
1. Создать класс, реализующий ChecksumCalculator
2. Добавить case в ChecksumCalculatorMain.createCalculator()
3. Написать модульные тесты

Для добавления нового формата отчета:
1. Расширить ReportFormat enum
2. Добавить методы в ReportWriter
3. Обновить логику в ChecksumCalculatorMain