# 💾 Storage Module - Сохранение и загрузка

## Описание
Этот модуль отвечает за работу с файлами контрольных сумм, парсинг результатов и сохранение/восстановление состояния сканирования.

## Классы

### ChecksumFileParser.java
**Парсер файлов контрольных сумм**.
- Загружает контрольные суммы из файла
- Поддерживает формат `md5sum` и `sha256sum`
- Игнорирует комментарии (строки начинающиеся с `#`)
- Нормализует пути для кроссплатформенности

**Формат файла:**
```
ff1b8f3d29e6c1e5f9d3a8c4b2e7f1a9 *src/file1.java
a5c9e2d1f4b8c3e7a9d2f1b5c8e3a7d4 *src/file2.java

# Это комментарий
e3b0c44298fc1c149afbf4c8996fb92 *empty.txt
```

**Примеры использования:**
```java
Map<String, String> checksums = ChecksumFileParser.parse(
    Paths.get("checksums/folder_sha256.txt")
);
```

### ScanMemento.java
**Снимок состояния сканирования** (паттерн Memento).
- Содержит полную информацию о состоянии на момент создания
- Позволяет сохранить и восстановить прогресс

**Сохраняемые данные:**
```
- Путь сканируемой директории
- Используемый алгоритм
- Режим следования символическим ссылкам
- Общее количество файлов
- Количество обработанных файлов
- Общий размер в байтах
- Обработано байтов
- Текущий файл
- Размер текущего файла
- Карта завершенных контрольных сумм
```

**Использование:**
```java
ScanMemento memento = processor.createMemento();
// ... сохранить memento ...
processor.restoreMemento(memento);
```

### StateStorage.java
**Управление сохранением/загрузкой состояния**.
- Использует `Properties` для сериализации
- Сохраняет состояние в файл
- Загружает состояние из файла

**Методы:**
```java
static void save(ScanMemento memento, Path stateFile) throws IOException
static ScanMemento load(Path stateFile) throws IOException
```

**Использование:**
```java
// Сохранение состояния
StateStorage.save(memento, Paths.get(".scan_state"));

// Восстановление состояния
ScanMemento restored = StateStorage.load(Paths.get(".scan_state"));
```

## Формат сохранения состояния

**Properties файл (`.scan_state`):**
```properties
rootPath=/path/to/scan
algorithm=sha256
followLinks=true
totalFiles=150
processedFiles=0
totalBytes=1048576
totalBytesProcessed=0
currentFile=null
currentFileBytes=0

# Завершенные контрольные суммы
file.src/main/java/ChecksumCalculator.java=ff1b8f3d29e6c1e5...
file.src/main/java/Md5Calculator.java=a5c9e2d1f4b8c3e7...
```

## Сценарии использования

### Сценарий 1: Порновая проверка целостности
```java
// Вычисление контрольных сумм
Map<String, String> checksums = processor.process(root);

// Сохранение результатов
ReportWriter.saveChecksumReport(checksums, 
    Paths.get("checksums/db_sha256.txt"));
```

### Сценарий 2: Проверка файлов
```java
// Загрузка ожидаемых контрольных сумм
Map<String, String> expected = ChecksumFileParser.parse(
    Paths.get("checksums/db_sha256.txt")
);

// Вычисление текущих контрольных сумм
Map<String, String> actual = processor.process(root);

// Сравнение результатов
```

### Сценарий 3: Возобновление прерванного сканирования
```java
// Сохранение состояния перед паузой
ScanMemento state = processor.createMemento();
StateStorage.save(state, Paths.get(".scan_state"));

// Позже: восстановление состояния
ScanMemento restored = StateStorage.load(Paths.get(".scan_state"));
processor.restoreMemento(restored);
```

## Паттерны проектирования
- **Memento**: Сохранение и восстановление состояния сканирования

## Расширение модуля

Для поддержки других форматов контрольных сумм:
1. Создать новый класс парсера, наследующий логику
2. Добавить методы для парсинга специфического формата
3. Интегрировать в `ChecksumCalculatorMain`
