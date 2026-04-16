# 🔐 ChecksumCalculator - Калькулятор Контрольных Сумм

Мощное Java приложение для вычисления и проверки контрольных сумм файлов и директорий. Поддерживает MD5 и SHA-256 алгоритмы с интерактивным управлением процессом.

## 📋 Структура проекта

```
src/main/java/
├── checker/
│   ├── cli/                     💻 Интерфейс командной строки
│   │   ├── ChecksumCalculatorMain.java
│   │   ├── CommandLineInterface.java
│   │   ├── CommandLineOptions.java
│   │   └── README.md
│   │
│   ├── core/                    🔐 Основной функционал
│   │   ├── ChecksumCalculator.java
│   │   ├── Md5Calculator.java
│   │   ├── Sha256Calculator.java
│   │   ├── ChecksumProcessor.java
│   │   └── README.md
│   │
│   ├── filesystem/              📁 Работа с файловой системой
│   │   ├── FileSystemNode.java
│   │   ├── FileNode.java
│   │   ├── DirectoryNode.java
│   │   ├── FileSystemBuilder.java
│   │   └── README.md
│   │
│   ├── reporting/               📊 Отчеты и результаты
│   │   ├── ReportWriter.java
│   │   ├── ReportFormat.java
│   │   ├── VerificationResult.java
│   │   ├── VerificationStatus.java
│   │   └── README.md
│   │
│   ├── storage/                 💾 Сохранение и загрузка
│   │   ├── ChecksumFileParser.java
│   │   ├── StateStorage.java
│   │   ├── ScanMemento.java
│   │   └── README.md
│   │
│   └── control/                 🎮 Управление сканированием
│       ├── ScanController.java
│       ├── ProgressInputStream.java
│       └── README.md
│
├── progress/                    📈 Отслеживание прогресса
│   ├── Observer.java
│   ├── Observable.java
│   ├── ProgressMessage.java
│   ├── ProgressReporter.java
│   ├── ProgressState.java
│   ├── ConsoleObserver.java
│   └── README.md
│
└── test/                        🧪 Тесты
    ├── checker/
    └── progress/
```

## 🎯 Модули

### 1. **CLI** - Интерфейс командной строки
Взаимодействие пользователя с программой:
- Парсинг аргументов командной строки
- Запрос пути у пользователя
- Вывод справки и информации

📖 [Подробнее](./checker/cli/README.md)

### 2. **Core** - Основной функционал
Вычисление контрольных сумм:
- Интерфейс `ChecksumCalculator`
- Реализации: MD5, SHA-256
- Главный обработчик `ChecksumProcessor`

📖 [Подробнее](./checker/core/README.md)

### 3. **FileSystem** - Работа с файловой системой
Построение иерархии файлов и папок:
- Иерархия узлов (Composite паттерн)
- Обнаружение циклов в ссылках
- Вычисление общего размера директорий

📖 [Подробнее](./checker/filesystem/README.md)

### 4. **Reporting** - Отчеты и результаты
Форматирование и вывод результатов:
- Поддержка TEXT и JSON форматов
- Результаты проверки файлов
- Нормализация путей

📖 [Подробнее](./checker/reporting/README.md)

### 5. **Storage** - Сохранение и загрузка
Работа с файлами контрольных сумм и состоянием:
- Парсинг файлов контрольных сумм
- Сохранение/загрузка состояния сканирования
- Memento паттерн для восстановления

📖 [Подробнее](./checker/storage/README.md)

### 6. **Control** - Управление сканированием
Контроль процесса сканирования:
- Пауза и возобновление
- Потокобезопасность
- Graceful shutdown

📖 [Подробнее](./checker/control/README.md)

### 7. **Progress** - Отслеживание прогресса
Система уведомлений о прогрессе (Observer паттерн):
- Наблюдатели за процессом
- Вывод прогресса в консоль
- Расширяемая архитектура

📖 [Подробнее](./progress/README.md)

## 🚀 Запуск программы

### Базовое использование (запросит путь)
```bash
java -cp target/classes checker.cli.ChecksumCalculatorMain
```

Программа попросит указать путь:
```
═══════════════════════════════════════════════════════
   🔐 CHECKSUM CALCULATOR - Калькулятор контрольных сумм
═══════════════════════════════════════════════════════

📁 Введите путь до файла или папки для сканирования: /path/to/folder
```

### С указанием пути (аргумент)
```bash
java -cp target/classes checker.cli.ChecksumCalculatorMain -p=/path/to/folder
```

### Проверка контрольных сумм
```bash
java -cp target/classes checker.cli.ChecksumCalculatorMain \
  -m=verify \
  -p=. \
  -c=checksums/folder_sha256.txt
```

### Использование MD5
```bash
java -cp target/classes checker.cli.ChecksumCalculatorMain \
  -p=/path \
  -a=md5
```

## ⌨️ Интерактивные команды

Во время сканирования введите:
```
pause   - Приостановить сканирование
resume  - Возобновить сканирование
quit    - Завершить программу
```

## 📊 Результаты

Контрольные суммы сохраняются в папке `checksums/`:
```
checksums/
├── folder_sha256.txt          # SHA-256 суммы
├── database_sha256.txt
└── source_md5.txt             # MD5 суммы
```

**Формат файла:**
```
ff1b8f3d29e6c1e5f9d3a8c4b2e7f1a9 *src/main/java/ChecksumCalculator.java
a5c9e2d1f4b8c3e7a9d2f1b5c8e3a7d4 *src/main/java/Md5Calculator.java
```

## 🏗️ Архитектурные паттерны

| Паттерн | Использование |
|---------|--------------|
| **Strategy** | Различные алгоритмы хеширования (MD5, SHA-256) |
| **Observer** | Система отслеживания прогресса |
| **Composite** | Иерархия узлов файловой системы |
| **Memento** | Сохранение/восстановление состояния |
| **Builder** | Построение дерева файловой системы |
| **Decorator** | Обертка потока для отслеживания прогресса |

## 🧪 Тестирование

```bash
# Компиляция с тестами
javac -cp "target/classes;lib/*" -d target/test-classes \
  src/test/java/checker/*.java

# Запуск тестов
java -cp "target/classes:target/test-classes:lib/*" \
  org.junit.platform.console.ConsoleLauncher --scan-classpath
```

## 📝 Примеры использования

### Пример 1: Вычисление SHA-256 для проекта
```bash
java -cp target/classes checker.cli.ChecksumCalculatorMain \
  -p=src \
  -a=sha256
```

Результат сохранится в `checksums/src_checksums_sha256.txt`

### Пример 2: Проверка целостности
```bash
java -cp target/classes checker.cli.ChecksumCalculatorMain \
  -m=verify \
  -p=data \
  -c=checksums/data_sha256.txt
```

Выведет результаты:
- ✅ OK - контрольная сумма совпадает
- ❌ MODIFIED - файл изменен
- ⚠️ NEW - новый файл
- ⚠️ REMOVED - файл удален

## 🔧 Расширение проекта

### Добавить новый алгоритм хеширования
1. Создать класс в `core/` наследующий `ChecksumCalculator`
2. Реализовать метод `calculate()`
3. Зарегистрировать в `ChecksumCalculatorMain.createCalculator()`

### Добавить новый формат отчета
1. Добавить значение в `ReportFormat` enum
2. Добавить методы в `ReportWriter`
3. Реализовать форматирование

### Добавить новый наблюдатель прогресса
1. Создать класс, реализующий `Observer`
2. Реализовать метод `update(ProgressMessage message)`
3. Добавить в главном классе через `reporter.addObserver()`

## 📚 Документация

Каждый модуль содержит собственный `README.md` с:
- Описанием функциональности
- Примерами использования
- Информацией о паттернах проектирования
- Рекомендациями по расширению

## 📞 Справка

```bash
java -cp target/classes checker.cli.ChecksumCalculatorMain --help
```

Выведет полную справку со всеми опциями и примерами.

---

**Автор:** ChecksumCalculator Team  
**Лицензия:** MIT  
**Java версия:** 11+  
**Последнее обновление:** 2026-04-13
