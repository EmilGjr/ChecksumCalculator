
# 🎮 Control Module – Управление на сканирането

## Описание

Този модул отговаря за управлението на състоянието на сканирането на файлове — пауза, възобновяване и спиране. Осигурява безопасна работа между нишки чрез синхронизация.

 ## Класове

### ScanController.java

Контролер на състоянието на сканирането.

**Отговорности:**
- Управлява пауза и възобновяване
- Осигурява безопасност при работа с нишки чрез synchronized
- Поддържа коректно спиране (graceful shutdown)
  
**Състояния:**

- paused = false — сканирането работи
- paused = true — сканирането е на пауза
- stopRequested = true — заявка за спиране
  
**Методи:**
```java
public void pause()                 // Поставяне на пауза
public void resume()                // Възобновяване
public boolean isPaused()           // Проверка дали е на пауза
public boolean isStopRequested()    // Проверка за заявка за спиране
public void requestStop()           // Заявка за спиране
public void waitIfPaused()          // Изчакване, ако е на пауза
```

 **Примери за използване:**
```java
ScanController controller = new ScanController();

// Пауза
controller.pause();
System.out.println("Paused: " + controller.isPaused()); // true

// Възобновяване
controller.resume();

// Работеща нишка
while (!controller.isStopRequested()) {
    controller.waitIfPaused();  // Изчаква, ако е на пауза
    // ... изпълнява сканиране ...
}
```


### ProgressInputStream.java

Обвивка на InputStream за следене на прогреса.
- Наследява FilterInputStream
- Уведомява слушател при прочитане на всеки блок данни
- Използва се за точно следене на байтовете при изчисляване на хешове
  
### Използване:
```java
InputStream fileStream = Files.newInputStream(filePath);

// Създаване на обвивка със слушател за прогрес
ProgressInputStream progressStream = new ProgressInputStream(
    fileStream,
    bytesRead -> {
        totalBytes += bytesRead;
        progressReporter.reportProgress(totalBytes);
    }
);

// Използване като нормален поток
String hash = calculator.calculate(progressStream);
```


### Поток на изпълнение при сканиране
```java
Main Thread                    Input Thread (daemon)
-----------                    ---------------------
build FileTree
create Controller              read "pause"
start Input Thread             |
|                              pause Controller
process files
|-- check isPaused() ✓         read "resume"
|-- waitIfPaused() ✓ BLOCKED   resume Controller
|                              |
|-- waitIfPaused() ✓ continue  read "quit"
|-- calculate hash             request Stop
|-- check isPaused() ✓         |
...                            exit Input Thread
join Thread (100ms)
exit
```


Сценарии на използване
### Сценарий 1: Нормално сканиране
```java
while (!controller.isStopRequested()) {
    controller.waitIfPaused();
    // ... обработка на файл ...
}
```

### Сценарий 2: Пауза и възобновяване
```java
// Потребителят въвежда "pause"
controller.pause();

// Сканирщият поток:
if (controller.isPaused()) {
    controller.waitIfPaused();  // Блокира тук
}

// Потребителят въвежда "resume"
controller.resume();  // Събужда чакащата нишка
```

### Сценарий 3: Спиране
```java
// Потребителят въвежда "quit"
controller.requestStop();
controller.resume();  // Събужда, ако е на пауза

// Основният цикъл приключва
while (!controller.isStopRequested()) {  // false → изход
    ...
}
```


### Дизайн патърни
- Monitor Object — синхронизация чрез обект заключване
- Decorator — ProgressInputStream обвива оригиналния поток

Безопасност при работа с нишки

### Класът използва:
```java
private volatile boolean paused;        // Видим за всички нишки
private volatile boolean stopRequested; // Видим за всички нишки
private final Object lock = new Object(); // За синхронизация
```

- volatile — гарантира видимост на промените между нишки
- synchronized — взаимно изключване при промяна на състоянието
- Object.wait() и Object.notifyAll() — координация между нишки
