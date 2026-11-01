# HUB — аудит перед бандлом «витрина»

Дата: 2026-11-01. Ветка `feature/hub-mockup`. Коммит базы:
`3afaaef v.0.1.6.3.6 phase 5 - redisigning hubscreen`.

Легенда: ✅ сделано · 🟡 частично · ❌ нет.

## 1. Карта: мокап ↔ код

| Элемент мокапа | Компонент в коде | Статус | Стадия |
|---|---|---|---|
| Фон `#141414` edge-to-edge | `HubScreen.Box(background = Color.Gray)` | ❌ фон серый | D |
| Top scrim `#141414 .88→0` 64dp | — | ❌ нет компонента | B |
| Bottom scrim `#141414 .92→0` 90dp | — | ❌ нет компонента | B |
| Статус-строка «15:40» встроена | `MainActivity.enableEdgeToEdge()` есть; `Scaffold` навешивает инсеты | 🟡 insets уже из Scaffold | D |
| DayHeader (дата + «Сегодня» + weather 40dp) | `formatDateHeader()` + `DayHeaderWeatherIcon` живут отдельно; в HubScreen НЕ собраны | ❌ нет сборки | B |
| StickyCapline «ПЯТНИЦА · 4 ИЮЛЯ» 24dp | — | ❌ нет | B |
| NowLine (жёлтая точка 7dp + линия 1dp) | — (есть `rememberNow()` для времени) | ❌ нет компонента | B |
| GlassFab 56dp белый `+` | — | ❌ нет (экран ссылается на `onExpensesClicked` через navigator, но FAB не нарисован) | B |
| Ритм 6/16/14 | `TimelineSection` рендерит без spacing — отступы в самих row'ах | ❌ нет функции gap | D |
| DayHeader shrink + sticky capline при скролле | `verticalScroll(rememberScrollState)` без sticky | ❌ нет (нужен LazyColumn) | D |
| TOMORROW секция (opacity 42% + заголовок 60%) | `DaySection` в state есть; label «Завтра»; но рендер не отличается от today | 🟡 state готов, UI не разделяет | D |
| EventRow (height ∝ длительности, 2dp border, amber CONFIRMED, 42% future) | один базовый `EventRow`, без высоты и border, без 42%, бейдж `CONFIRMED` отсутствует (но и TODO/краша нет — удалён ранее) | 🟡 skeleton есть | C |
| ExpenseRow (жёлтая сумма DM Serif + точка 5dp, стекло 5.5%, без border/glow) | есть, точка жёлтая, сумма amountExpense; стекло `Glass.body` (4.5%) — расходится с 5.5% в токенах | 🟡 цвет стекла путает | C |
| HealthDigitRow (`SourceStamp` 26dp для exercise, стекло 4.5%, tnum) | эмодзи в круге 26dp вместо `SourceStamp`; `item.source` НЕ используется в UI | 🟡 поле source есть в модели и mapper, UI не рисует | C |
| QuestionCard одна, после NowLine | `QuestionCard` уже встроен ДО TimelineSection (не внутрь ленты, не после NowLine) | 🟡 компонент есть, место неверное | D |
| nested expense под событием (connector 2dp) | нет; маппер плоский | ❌ | E |
| Weather: `WeatherFan` + `LiveGlassOverlay` | компоненты есть, но `WeatherFan` НЕ использован сейчас в HubScreen (импорт отмечен); `LiveGlassOverlay` используется | 🟡 WeatherFan orphan | D |
| HealthConnectGhostBlock (NoPermission) | есть в `HubScreen.kt:339` с `Color.White`/`Color.Gray` литералами | 🟡 литералы вместо токенов | D |

## 2. Регрессии стадии A — что уже выполнено до бандла

| Пункт брифа | Статус | Комментарий |
|---|---|---|
| A1. `now` из сигнатуры TimelineMapper | ✅ | Параметра `now` в `TimelineMapper.invoke(...)` нет. |
| A1. `isPast` из `TimelineItem` | ✅ | В sealed `TimelineItem` только `startsAt/sortKey`, `isFuture` вычисляется в месте вызова. |
| A1. `nowTicker` из VM | ✅ | В `HubViewModel` нет `nowTicker`-поля. |
| A2. Тикер в UI (`rememberNow`) | 🟡 | `rememberNow()` существует (`feature/hub/.../util/RememberNow.kt`), выравнен по минуте. **Clock не внедрён через Koin/CompositionLocal** — пока жёстко `Clock.System`. Нужно расширить под демо (стадия F). |
| A3. Смена суток в VM (midnight re-emit) | ❌ | `_refreshTrigger` эмитит на старт/ON_RESUME; midnight-авто-эмита нет. Доделать. |
| A4. Multi-day в `HubState` (`DaySection`) | ✅ | `DaySection(date, label, weatherIcon, items)` + `HubState.days`. **Отсутствует `DayKind` enum** — бриф хочет `TODAY/TOMORROW`, сейчас есть строковый `label`. Добавлю. |
| A5. `stableId: String` в `TimelineItem` | ✅ | Поле есть во всех трёх вариантах. Формат совпадает с бриф-примером `expense-$id`, `event-$id-$ts`, `health-sleep-$date`. |
| A6. source stamp — `TimelineMapper.mapExercise` прокидывает `e.source` | ✅ | `TimelineItem.HealthDigit.source` есть, mapper проставляет. **UI пока его не рендерит через `SourceStamp`** → фикс в стадии C. |
| A7. `IntentionBadge` + `TODO()` | ✅ | В `EventRow.kt` блока `IntentionBadge` нет; остался корректный `🎯`-клик для `intentionState == null`. `grep "TODO()" feature/ core/` — 0 попаданий (единственное попадание в brief-примере про F-04 — комментарий `// TODO F-04`, не рантайм). |
| A8. Health-чипы в ленте | 🟡 | В `TimelineMapper` sleep/steps/exercise превращаются в `TimelineItem.HealthDigit`, в `HubScreen` рендерятся через `HealthDigitRow`. Регрессия «пропали из шапки» в STATUS относится к старой композиции. На устройстве владелец должен проверить при `HealthState.Available`. |
| A9. `grep -rn "TODO()" feature/ core/` | ✅ | 0 попаданий. |

## 3. Orphan-код в Hub

| Символ | Файл | Call sites | Действие |
|---|---|---|---|
| `GlassWidgetCard` | `HubScreen.kt:209` | 0 | удалить (стадия D) |
| `DayHeaderWeatherIcon` | `WeatherComponent.kt` | import есть в `HubScreen.kt` но композиция НЕ вызывается | удалить import; сам компонент оставить — переиспользуем в DayHeader (B1) |
| `WeatherFan` | `WeatherComponent.kt` | import есть, вызов НЕ выполняется | удалить import; `WeatherFan` оставить (используется с tap по погоде, вернётся при верстке DayHeader) |
| `LiveGlassOverlay` | `WeatherComponent.kt` | вызывается в `HubScreen.kt:200` | оставить (атмосферный эффект) |
| `HubState.dailyExpenses` / `upcomingEventsCount` | — | уже отсутствуют в текущем `HubState` | ничего делать не надо |
| `Color.Gray` фон | `HubScreen.kt:128` | — | → `#141414` через `AppColors.PureBlack` или `SafeSpaceTokens.Palette.ink` (D) |

## 4. Расхождения с мокапом (ранние заметки, подтвердить при верстке)

- Валюта в мокапе `$`, продукт для KZ. Будем рисовать `₸` в демо-данных (запись в hub-bundle-report.md: решение бандла).
- All-day события в мокапе не показаны — отрисуем компактно 56dp без гаттера-времени.
- В мокапе NowLine стоит ПОСЛЕ прошедших, перед future-ивентом и QuestionCard. Совпадает с §4.7 брифа.
- `rgba(255,255,255,.045)` в мокапе для Health-карточек: совпадает с `SafeSpaceTokens.Glass.body` (0.045) — ОК.
- `rgba(255,255,255,.055)` для трат vs токен `Glass.expense` (0.055) — ОК.
- `rgba(255,255,255,.07)` для событий vs `Glass.event` (0.07) — ОК.
- Для карточки-вопроса в мокапе `.035`, у нас `AppColors.CardOutline` фон не задан (используется `.14` на border). Проверить при верстке D (сверить с `QuestionCard` composable).

## 5. Выводы для стадии A

Основная часть A уже сделана ранее. Остаются:
1. **A2** — расширить `rememberNow()` на `LocalAppClock` (понадобится для DemoClock в F).
2. **A3** — добавить midnight re-emit в `daysDataFlow` ViewModel'и.
3. **A4** — добавить enum `DayKind { TODAY, TOMORROW }` в `DaySection`, использовать вместо строкового `label`.
4. Готовность: `./gradlew assembleDebug` и `./gradlew test` зелёные.

Остальные пункты (source stamp UI, Health регрессия, orphan удаление) — переезжают в C/D по месту.
