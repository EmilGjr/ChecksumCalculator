# 🔐 ChecksumCalculator

**Калькулятор контрольных сумм** - Java приложение для вычисления и проверки целостности файлов.

## ✨ Основные возможности

- ✅ Вычисление контрольных сумм (MD5, SHA-256)
- ✅ Проверка целостности файлов
- ✅ Интерактивное управление (пауза, возобновление)
- ✅ Мониторинг прогресса в реальном времени
- ✅ Сохранение/восстановление состояния сканирования
- ✅ Экспорт в TEXT и JSON форматы
- ✅ Поддержка больших директорий

## 🚀 Быстрый старт

### Запуск (программа запросит путь)
```bash
java -cp target/classes checker.cli.ChecksumCalculatorMain
```

### Запуск с указанием папки
```bash
java -cp target/classes checker.cli.ChecksumCalculatorMain -p=./src
```

### Проверка целостности
```bash
java -cp target/classes checker.cli.ChecksumCalculatorMain \
  -m=verify -p=. -c=checksums/src_checksums_sha256.txt
```

## 📖 Документация

- 📚 [Полное описание структуры](./PROJECT_STRUCTURE.md)
- 📽️ [Пользовательское руководство](./GUIDE.md)
- 📝 [Документация проекта](./ProjectDocumentation.md)

## 🏗️ Архитектура модулей

```
checker.cli/           - 💻 Интерфейс командной строки
checker.core/          - 🔐 Основной функционал  
checker.filesystem/    - 📁 Работа с файловой системой
checker.reporting/     - 📊 Отчеты и результаты
checker.storage/       - 💾 Сохранение и загрузка
checker.control/       - 🎮 Управление сканированием
progress/              - 📈 Отслеживание прогресса
```

[➡️ Подробнее о структуре](./PROJECT_STRUCTURE.md)

## ⌨️ Команды управления

Во время работы введите:
```
pause  → Приостановить
resume → Возобновить
quit   → Завершить
```

## 📊 Результаты

Контрольные суммы сохраняются в папке `checksums/`:
```
checksums/
├── folder_sha256.txt
├── src_checksums_sha256.txt
└── project_md5.txt
```

Формат совместим с `md5sum`/`sha256sum`:
```
ff1b8f3d... *src/ChecksumCalculator.java
a5c9e2d1... *src/Md5Calculator.java
```

## 🔧 Компиляция

```bash
# Скомпилировать всё
javac -cp "lib/*" -d target/classes \
  src/main/java/checker/**/*.java \
  src/main/java/progress/**/*.java
```

## 🧪 Тестирование

```bash
# Запустить тесты
java -cp "target/classes:target/test-classes:lib/*" \
  org.junit.platform.console.ConsoleLauncher --scan-classpath
```

## 📚 Опции командной строки

```
-p=<path>         Путь до файла/папки (по умолчанию запросит)
-m=<mode>         Режим: calculate (по умолчанию), verify
-a=<algorithm>    Алгоритм: sha256 (по умолчанию), md5
-c=<file>         Файл контрольных сумм
-t=<file>         Файл результатов проверки
-nfl              Не следовать символическим ссылкам
--help            Показать справку
```

## 📁 Структура папок

```
ChecksumCalculator/
├── src/
│   ├── main/java/
│   │   ├── checker/       (основной код)
│   │   └── progress/      (прогресс)
│   └── test/java/
├── target/                (скомпилированные классы)
├── lib/                   (зависимости)
├── checksums/             (сохраненные контрольные суммы)
├── GUIDE.md               (руководство пользователя)
├── PROJECT_STRUCTURE.md   (архитектура проекта)
└── ProjectDocumentation.md (подробная документация)
```

## 🎯 Примеры

### Пример 1: Сканирование папки
```bash
$ java -cp target/classes checker.cli.ChecksumCalculatorMain

═══════════════════════════════════════════════════════
   🔐 CHECKSUM CALCULATOR - Калькулятор контрольных сумм
═══════════════════════════════════════════════════════

📁 Введите путь до файла или папки для сканирования: src

🔍 Сканирање: C:\...\ChecksumCalculator\src
📋 Алгоритъм: SHA256
▶️  Запуск...

[прогресс сканирования...]

✅ Контрольные суммы сохранены: checksums\src_checksums_sha256.txt
```

### Пример 2: Проверка целостности
```bash
$ java -cp target/classes checker.cli.ChecksumCalculatorMain \
  -m=verify -p=src -c=checksums/src_checksums_sha256.txt

[результаты проверки...]

File1.java: OK
File2.java: MODIFIED (файл был изменен!)
File3.java: REMOVED
NewFile.java: NEW
```

## 🔍 Поиск информации

- 🔐 О вычислении контрольных сумм → [core/README.md](./src/main/java/checker/core/README.md)
- 📁 О структуре файловой системы → [filesystem/README.md](./src/main/java/checker/filesystem/README.md)
- 📊 О форматах отчетов → [reporting/README.md](./src/main/java/checker/reporting/README.md)
- 💾 О сохранении состояния → [storage/README.md](./src/main/java/checker/storage/README.md)
- 📈 Об отслеживании прогресса → [progress/README.md](./src/main/java/progress/README.md)

## 📝 Требования

- Java 11 или выше
- JUnit 5 (для тестов)

## 🤝 Расширение

Проект легко расширяется:
- ➕ Добавить новый алгоритм хеширования
- ➕ Добавить новый формат отчета
- ➕ Добавить новый тип наблюдателя для прогресса

[Подробнее о расширении](./PROJECT_STRUCTURE.md)

---

**Версия:** 1.0.0  
**Статус:** ✅ Готово к использованию  
**Последнее обновление:** 2026-04-13
