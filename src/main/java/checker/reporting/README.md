# 📊 Reporting Module - Отчеты и результаты

## Описание
Этот модуль отвечает за форматирование и вывод результатов работы приложения. Поддерживает несколько форматов вывода и сохранения отчетов.

## Классы

### ReportFormat.java
**Перечисление** поддерживаемых форматов отчетов.
```java
public enum ReportFormat {
    TEXT,
    JSON;
}
```
- `TEXT` - простой текстовый формат (для md5sum/sha256sum)
- `JSON` - структурированный JSON формат

### VerificationResult.java
**Перечисление** результатов проверки файлов.
```java
public enum VerificationResult {
    OK,        // Контрольная сумма совпадает
    MODIFIED,  // Контрольная сумма изменилась
    NEW,       // Файл новый (не в списке проверки)
    REMOVED    // Файл удален (был в списке, но не найден)
}
```

### VerificationStatus.java
**Класс** для хранения статуса проверки отдельного файла.
```java
public class VerificationStatus {
    private final String path;
    private final VerificationResult result;
    private final String expectedChecksum;
    private final String actualChecksum;
}
```
- Путь до файла
- Результат проверки (OK, MODIFIED, NEW, REMOVED)
- Ожидаемая контрольная сумма
- Фактическая контрольная сумма

### ReportWriter.java
**Главный класс** для записи отчетов.
- Поддерживает несколько форматов вывода
- Сортирует результаты по пути
- Реализует нормализацию путей (Windows → Unix)

**Методы:**
```java
// Вывод контрольных сумм
static void printChecksumReport(Map<String, String> results, ReportFormat format)
static void saveChecksumReport(Map<String, String> results, Path file)

// Вывод результатов проверки
static void printVerificationReport(List<VerificationStatus> statuses, ReportFormat format)
static void saveVerificationReport(List<VerificationStatus> statuses, Path file)
```

## Форматы вывода

### TEXT (по умолчанию)
```
ff1b8f3d29e6c1e5f9d3a8c4b2e7f1a9 *src/main/java/checker/ChecksumCalculator.java
a5c9e2d1f4b8c3e7a9d2f1b5c8e3a7d4 *src/main/java/checker/Md5Calculator.java
```

### JSON
```json
[
  {
    "path": "src/main/java/checker/ChecksumCalculator.java",
    "checksum": "ff1b8f3d29e6c1e5f9d3a8c4b2e7f1a9",
    "mode": "binary"
  },
  ...
]
```

## Примеры использования

```java
// Сохранение контрольных сумм
Map<String, String> checksums = ...;
Path reportFile = Paths.get("checksums/folder_sha256.txt");
ReportWriter.saveChecksumReport(checksums, reportFile);

// Вывод результатов проверки
List<VerificationStatus> statuses = ...;
ReportWriter.printVerificationReport(statuses, ReportFormat.TEXT);

// Сохранение результатов проверки
ReportWriter.saveVerificationReport(statuses, Paths.get("verification_report.txt"));
```

## Структура отчета

Контрольные суммы сохраняются в формате, совместимом с `md5sum` и `sha256sum`:
```
<хеш> *<путь>
```

Результаты проверки содержат пути и статусы:
```
src/file.java: OK
src/deleted.java: REMOVED
src/new.java: NEW
src/changed.java: MODIFIED
```

## Расширение модуля

Для добавления нового формата отчета:
1. Добавить значение в `ReportFormat` enum
2. Добавить методы `print<Format>()` и `save<Format>()` в `ReportWriter`
3. Реализовать форматирование соответствующего формата
