# 📈 Progress Module - Отслеживание прогресса

## Описание
Этот модуль реализует систему отслеживания прогресса сканирования через паттерн **Observer**. Позволяет различным компонентам получать уведомления о ходе работы.

## Классы

### Observer.java
**Интерфейс наблюдателя**.
```java
public interface Observer {
    void update(ProgressMessage message);
}
```
- Определяет контракт для получения уведомлений о прогрессе
- Позволяет добавлять любые реализации наблюдателей

### Observable.java
**Базовый класс** для наблюдаемых объектов.
```java
public abstract class Observable {
    protected List<Observer> observers = new ArrayList<>();
    
    public void addObserver(Observer observer);
    public void removeObserver(Observer observer);
    public void notifyObservers(ProgressMessage message);
}
```
- Управляет списком наблюдателей
- Уведомляет всех наблюдателей об изменениях

### ProgressMessage.java
**Сообщение о прогрессе**.
```java
public class ProgressMessage {
    private final ProgressState state;
    private long filesProcessed;
    private long totalFiles;
    private long bytesProcessed;
    private long totalBytes;
    private String currentFile;
}
```
- Содержит информацию о текущем состоянии
- Передается наблюдателям при обновлении

### ProgressState.java
**Перечисление состояний прогресса**.
```java
public enum ProgressState {
    STARTED,        // Сканирование начато
    FILE_STARTED,   // Начата обработка файла
    FILE_COMPLETED, // Завершена обработка файла
    PAUSED,         // Сканирование на паузе
    RESUMED,        // Сканирование возобновлено
    COMPLETED,      // Сканирование завершено
    ERROR           // Произошла ошибка
}
```

### ProgressReporter.java
**Главный класс** для управления прогрессом.
- Наследует `Observable`
- Создает и отправляет сообщения о прогрессе
- Интегрируется с `ChecksumProcessor`

**Методы:**
```java
public void onScanStarted(long totalFiles, long totalBytes)
public void onFileStarted(String filePath, long fileSize)
public void onFileCompleted(String filePath)
public void onBytesProcessed(long bytes)
public void onScanPaused()
public void onScanResumed()
public void onScanCompleted()
public void onError(String message)
```

### ConsoleObserver.java
**Реализация наблюдателя** для вывода в консоль.
- Форматирует и выводит прогресс в консоль
- Показывает процент завершения
- Выводит текущий обрабатываемый файл
- Показывает скорость обработки

**Примеры вывода:**
```
Сканирование: 150 файлов, 102.4 МБ
Обработано: 45/150 (30%) - 30.7 МБ
Текущий файл: src/main/java/checker/ChecksumCalculator.java
Скорость: 5.2 МБ/сек
```

## Примеры использования

```java
// Создание системы прогресса
ProgressReporter reporter = new ProgressReporter();

// Добавление наблюдателя
ConsoleObserver consoleObserver = new ConsoleObserver();
reporter.addObserver(consoleObserver);

// Отправка сообщений о прогрессе
reporter.onScanStarted(150, 1048576);  // 150 файлов, 1 МБ
reporter.onFileStarted("file1.java", 1024);
reporter.onBytesProcessed(512);
reporter.onFileCompleted("file1.java");
...
reporter.onScanCompleted();
```

## Архитектура Observer

```
                    ┌─────────────────┐
                    │    Observable   │
                    │ (ProgressReport)│
                    └────────┬────────┘
                             │
                   ┌─────────┼─────────┐
                   │         │         │
              ┌────▼──┐┌─────▼───┐┌──┬▼┐
              │Console││ Custom1 ││..│N│
              │Observer││Observer││  │ │
              └────────┘└────────┘└──┴─┘
```

## Сценарии использования

### Сценарий 1: Вывод в консоль
```java
ProgressReporter reporter = new ProgressReporter();
reporter.addObserver(new ConsoleObserver());

// Сообщения автоматически выводятся консолью
```

### Сценарий 2: Логирование в файл
```java
// Создать пользовательский наблюдатель
Observer fileLogger = message -> {
    // Логировать в файл
};
reporter.addObserver(fileLogger);
```

### Сценарий 3: Обновление GUI
```java
// Создать наблюдателя для GUI
Observer guiUpdater = message -> {
    progressBar.setValue(
        (int) (message.getBytesProcessed() * 100 / message.getTotalBytes())
    );
};
reporter.addObserver(guiUpdater);
```

## Расширение модуля

Для добавления нового типа наблюдателя:
1. Создать класс, реализующий `Observer`
2. Реализовать метод `update(ProgressMessage message)`
3. Добавить в `ProgressReporter` через `addObserver()`

## Паттерны проектирования
- **Observer**: Система уведомлений о прогрессе
- **Strategy**: Различные реализации наблюдателей (консоль, логирование, GUI)
