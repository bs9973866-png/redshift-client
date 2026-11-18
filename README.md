# REDSHIFT CLIENT

Визуальный клиент для **Minecraft Java 1.21.11** на **Fabric**.
Только визуал: HUD, анимации взмаха, косметика, профили серверов. Никаких читов и обхода античитов.

> Статус: первая версия. Исходники написаны без возможности компиляции, поэтому первый запуск Actions может выявить ошибки API Minecraft 1.21.11. Присылайте лог.

## Что реализовано
- **HUD-модули** (переключатель, масштаб, прозрачность, цвет, фон, позиция): FPS, CPS, Coordinates, Ping, Sprint, Direction, Session Stats, Item Counter, Armor, Potion, Keystrokes, Custom Notifications.
- **Custom Crosshair** (4 стиля, размер, толщина, зазор).
- **HUD Editor**: перетаскивание мышью, сетка и привязка, привязка к центру, масштаб (колесо/кнопки), прозрачность, сброс.
- **Swing Animations** (только клиентский рендер): Classic, Smooth, Dynamic, Spin, Slide, Reverse, Custom; отдельные настройки для мечей и остальных предметов; скорость, угол, плавность, интенсивность, смещение руки и предмета; 2D-предпросмотр; сброс на ваниль.
- **Server Profiles**: default, FunTime, ReallyWorld, HolyWorld, TwixWorld + свои; автоопределение по подстроке адреса; в профиле HUD, анимации, косметика, акцентный цвет.
- **Cosmetics (визуальные эффекты)**: Trails, Auras, Particles, Footsteps, Ambient на клиентских частицах; редкость, избранное, коллекция, локальная разблокировка по времени игры, предпросмотр на модели игрока; свои эффекты через `config/redshift/cosmetics.json`.
- **Settings** с поиском, **Performance** (плотность эффектов, авто-снижение при FPS < 30, интервал обновления HUD).
- JSON-конфиг `config/redshift.json`.

## Не реализовано (пока)
- 3D-косметика (крылья, плащи, шапки, маски, очки, рюкзаки, питомцы): нужны модели и рендер-слои.
- Custom Hotbar / Scoreboard / Chat / Bossbar / Inventory / Fonts / Enchant Glint, Damage Particles, Item Animations.
- Горячие клавиши отдельно для каждого профиля (есть глобальные: меню, HUD Editor, показ HUD).
- Вращение предпросмотра ограничено (используется стандартный рендер сущности GUI).
- Проверка совместимости с Sodium и замеры производительности не проводились.

## Управление
Правый Shift: меню. Остальные клавиши: Настройки > Управление > Разное.

## Свои косметические эффекты
`config/redshift/cosmetics.json`:
```json
[{"id":"sparks","name":"Sparks","category":"TRAILS","rarity":"RARE","particle":"minecraft:electric_spark","count":2}]
```
Категории: TRAILS, AURAS, PARTICLES, FOOTSTEPS, AMBIENT. Частицы только «простые» (без параметров).

## Планируемые модули
`core`, `config`, `hud`, `animations`, `cosmetics`, `profiles`, `gui`, `rendering`, `visuals`, `utilities`

## Установка (для игроков)
1. Установи Java 21 и [Fabric Loader](https://fabricmc.net/use/) для 1.21.11.
2. Положи [Fabric API](https://modrinth.com/mod/fabric-api) в папку `mods`.
3. Скачай `redshift-client-x.y.z.jar` из раздела **Releases** (или из **Actions → Artifacts**) и положи в `mods`.
4. Запусти игру. Конфиг: `config/redshift.json`.

## Сборка на GitHub
1. Создай репозиторий и залей туда содержимое этой папки.
2. Открой вкладку **Actions**, workflow **Build** запустится сам при push (или вручную: *Run workflow*).
3. Готовый JAR: *Actions → запуск → Artifacts → redshift-client*.
4. Релиз: создай тег и запушь его:
   ```
   git tag v0.1.0
   git push origin v0.1.0
   ```
   JAR автоматически прикрепится к GitHub Release.

## Локальная сборка
```
gradle build          # или ./gradlew build, если добавлен wrapper
```
Результат: `build/libs/`. Запуск клиента для разработки: `gradle runClient`.

Рекомендуется один раз выполнить `gradle wrapper` и закоммитить `gradlew`, `gradlew.bat`, `gradle/wrapper/`.

## Если сборка падает
Версии в `gradle.properties` нужно сверить с https://fabricmc.net/develop (выбрать Minecraft 1.21.11):
`loader_version`, `loom_version`, `fabric_version`. Проект использует официальные маппинги Mojang.

## Правила проекта
Запрещены KillAura, AutoClicker, AimAssist, Reach, Velocity, Fly, Speed, NoFall, X-Ray, ESP, обход античита и подмена пакетов.

## Лицензия
MIT
