# 📁 FileSystem Module - Работа с файловой системой

## Описание
Этот модуль отвечает за построение иерархического представления файловой системы и работу с файлами и директориями. Использует паттерн **Composite** для создания дерева узлов.

## Классы

### FileSystemNode.java
**Абстрактный базовый класс** для всех узлов дерева файловой системы.
```java
abstract public class FileSystemNode {
    protected final String name;
    protected final Path path;
    
    public abstract long getSize();
    public String getRelativePath(Path root);
}
```
- Определяет общий интерфейс для файлов и директорий
- Предоставляет методы для получения имени и пути
- Абстрактный метод `getSize()` для вычисления размера

### FileNode.java
**Представление файла** в дереве файловой системы.
- Наследует `FileSystemNode`
- Хранит размер файла в байтах
- Является листовым узлом (не имеет дочерних элементов)

### DirectoryNode.java
**Представление директории** в дереве файловой системы.
- Наследует `FileSystemNode`
- Содержит список дочерних узлов
- Размер директории = сумма размеров всех дочерних элементов
- Рекурсивно вычисляет общий размер

### FileSystemBuilder.java
**Строитель дерева** файловой системы.
- Рекурсивно обходит файловую систему
- Обнаруживает циклы в символических ссылках
- Поддерживает настройку `followLinks` для исключения ссылок
- Использует `Files.newDirectoryStream()` для эффективного обхода

**Обнаружение циклов:**
- Отслеживает все посещенные пути через `Set<Path>`
- Каждый путь преобразуется в канонический путь (`toRealPath()`)
- Если путь уже посещен, выбрасывается исключение

## Структура дерева

```
root (DirectoryNode)
├── file1.txt (FileNode, 100 байт)
├── subdir1 (DirectoryNode)
│   ├── file2.txt (FileNode, 200 байт)
│   └── file3.txt (FileNode, 150 байт)
└── subdir2 (DirectoryNode)
    └── file4.txt (FileNode, 300 байт)

Total size: 750 bytes
```

## Примеры использования

```java
// Построение дерева
FileSystemBuilder builder = new FileSystemBuilder(true); // true = следовать ссылкам
FileSystemNode root = builder.build(Paths.get("/path/to/folder"));

// Получение информации
String name = root.getName();               // имя корневой папки
long totalSize = root.getSize();            // общий размер
Path relativePath = Paths.get(".");
String relativeStr = root.getRelativePath(relativePath);
```

## Паттерны проектирования
- **Composite**: Иерархия узлов (FileNode и DirectoryNode)
- **Builder**: `FileSystemBuilder` для построения дерева

## Расширение модуля

Для добавления поддержки новых типов узлов (например, символических ссылок):
1. Создать класс, наследующий `FileSystemNode`
2. Реализовать методы `getName()` и `getSize()`
3. Обновить `FileSystemBuilder.buildRecursive()`
