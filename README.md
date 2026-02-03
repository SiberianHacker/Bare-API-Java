# Bare-API-Java
Java библиотека для работы с Bare API ( https://bareapi.shop )
## Основные шаги

```java
BareAPI api = new BareAPI();

// 1. Инициализация (обязательно)
api.initBareAPI();

// 2. Установка ключа (обязательно)
api.setKey("ВАШ_API_КЛЮЧ");
// или
api.setKeyFromFile(new File("key.txt"));

// 3. (опционально) Настройки
api.setDelay(1200);       // задержка перед запросом результата, мс (мин 300)
api.setResize(true);      // приводить капчу к размеру 250×150
```

## Решение капчи

```java
// Поддерживаемые варианты передачи капчи
String answer = api.solveCaptcha("captcha.png");        // путь к файлу
// или
String answer = api.solveCaptcha(new File("captcha.jpg"));
// или
String answer = api.solveCaptcha(ImageIO.read(new File("cap.png")));
// или
String answer = api.solveCaptcha(bufferedImage);
```

## Полный пример

```java
public class Example {
    public static void main(String[] args) throws Exception {
        BareAPI api = new BareAPI();

        if (!api.initBareAPI()) {
            System.out.println("Не удалось получить адрес API");
            return;
        }

        api.setKey("ВАШ_API_КЛЮЧ");
        api.setDelay(1000);
        api.setResize(true);

        try {
            String result = api.solveCaptcha("captcha.png");
            System.out.println("Ответ: " + result);
        } catch (BareAPIException e) {
            System.out.println("Ошибка API: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
