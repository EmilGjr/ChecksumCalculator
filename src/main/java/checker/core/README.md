# 🔐 Core Module - Основной функционал вычисления хешей

## Описание
Этот модуль содержит основную логику для вычисления контрольных сумм файлов. Используется паттерн **Strategy** для поддержки различных алгоритмов хеширования.

## Классы

### ChecksumCalculator.java
**Интерфейс** для всех калькуляторов контрольных сумм.
```java
public interface ChecksumCalculator {
    String calculate(InputStream input) throws IOException;
}
```
- Определяет контракт для вычисления хешей
- Позволяет расширять поддержку новых алгоритмов

### Md5Calculator.java
**Реализация** интерфейса для алгоритма MD5.
- Использует `MessageDigest.getInstance("MD5")`
- Читает данные блоками по 4096 байт
- Возвращает 32-символьную шестнадцатеричную строку

### Sha256Calculator.java
**Реализация** интерфейса для алгоритма SHA-256.
- Использует `MessageDigest.getInstance("SHA-256")`
- Более безопасен, чем MD5
- Возвращает 64-символьную шестнадцатеричную строку

### ChecksumProcessor.java
**Главный обработчик** для рекурсивного обхода файловой системы и вычисления хешей.
- Обходит дерево `FileSystemNode` рекурсивно
- Вычисляет хеш для каждого файла
- Интегрирует систему отчетов о прогрессе
- Поддерживает паузу/возобновление через `ScanController`
- Сохраняет состояние через `ScanMemento` (паттерн Memento)

## Примеры использования

```java
// Выбор алгоритма
ChecksumCalculator calculator = new Sha256Calculator();

// Вычисление контрольной суммы файла
FileInputStream fis = new FileInputStream("file.txt");
String hash = calculator.calculate(fis);
fis.close();

// Обработка директории
ChecksumProcessor processor = new ChecksumProcessor(calculator, reporter, controller);
Map<String, String> results = processor.process(root);
```

## Расширение модуля

Для добавления нового алгоритма хеширования:
1. Создать класс, реализующий `ChecksumCalculator`
2. Реализовать метод `calculate()`
3. Зарегистрировать в `ChecksumCalculatorMain.createCalculator()`

## Паттерны проектирования
- **Strategy**: Различные реализации `ChecksumCalculator`
- **Memento**: Сохранение состояния обработки
