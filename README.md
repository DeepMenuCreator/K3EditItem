# K3EditItem

**Масштабный плагин кастомных предметов для Paper 1.21.4**

## Возможности

- 24+ уникальных способности
- GUI редактор предметов
- Настройка анимаций
- Кастомные значения для способностей
- Система кулдаунов
- Сохранение в YAML

## Команды

| Команда | Описание | Права |
|---------|----------|-------|
| `/k3edititem open` | Открыть главное меню | k3edititem.use |
| `/k3edititem create <id> <material>` | Создать предмет | k3edititem.admin |
| `/k3edititem claim <id>` | Получить предмет | k3edititem.use |
| `/k3edititem delete <id>` | Удалить предмет | k3edititem.admin |
| `/k3edititem list` | Список предметов | k3edititem.use |
| `/k3edititem reload` | Перезагрузить конфиг | k3edititem.admin |

## Способности (24 штуки)

1. Огненный Шар (Fireball)
2. Паутинная Ловушка (Web Trap)
3. Молния (Lightning)
4. Телепорт (Teleport)
5. Исцеление (Heal)
6. Взрыв (Explosion)
7. Заморозка (Freeze)
8. Рывок (Dash)
9. Гарпун (Grapple)
10. Энергетический Щит (Shield)
11. Ядовитое Облако (Poison)
12. Невидимость (Invisibility)
13. Лазер (Laser)
14. Торнадо (Tornado)
15. Черная Дыра (Black Hole)
16. Призыв (Summon)
17. Остановка Времени (Time Stop)
18. Метеорит (Meteor)
19. Цепная Молния (Chain)
20. Обмен (Swap)
21. Отражение (Reflect)
22. Берсерк (Berserk)
23. Клонирование (Clone)
24. Феникс (Phoenix) — пассивное воскрешение

## Анимации (8 штук)

- Круговая
- Спиральная
- Пульсация
- Луч
- Аура
- След
- Кольца
- Звезда

## Сборка

### Локально (Maven)
```bash
mvn clean package
```

### GitHub Actions
Пуш в `main` или `master` автоматически соберет `.jar` и создаст релиз.

## Установка

1. Скопируй `K3EditItem-1.0.0.jar` в `plugins/`
2. Перезапусти сервер
3. Используй `/k3edititem open`

## Требования

- Paper 1.21.4+
- Java 21+
- Maven 3.9+ (для сборки)

## Лицензия

MIT