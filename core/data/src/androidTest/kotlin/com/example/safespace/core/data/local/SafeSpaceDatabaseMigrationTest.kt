package com.example.safespace.core.data.local

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.safespace.core.data.local.SafeSpaceDatabase.Companion.MIGRATION_6_7
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * День 15, Slot A — MIGRATION_5_6 (добавление колонки recurringEventId).
 *
 * Что проверяем:
 * 1) Существующая v5-строка после миграции доступна и данные не потерялись.
 * 2) Новая колонка recurringEventId существует и по умолчанию NULL для
 *    строк, вставленных до миграции (SQLite так работает с nullable ADD COLUMN).
 * 3) После миграции можно записать значение в recurringEventId и прочитать
 *    его обратно через v6-схему (Room valides identityHash сам).
 *
 * Почему androidTest, а не test/: MigrationTestHelper тянет реальный
 * SupportSQLiteOpenHelper (Android SDK), которого нет в JVM. Robolectric —
 * альтернатива, но она добавляет сложность ради того же результата.
 *
 * Требует: schemas/5.json (hand-crafted) + schemas/6.json (сгенерирован KSP).
 */
@RunWith(AndroidJUnit4::class)
class SafeSpaceDatabaseMigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        SafeSpaceDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory(),
    )

    @Test
    @Throws(IOException::class)
    fun migrate5To6_preservesExistingRow_addsNullableRecurringEventId() {
        // 1) Создаём v5-базу и вставляем строку старой формы (без recurringEventId).
        helper.createDatabase(TEST_DB, 5).use { db ->
            db.execSQL(
                """
                INSERT INTO expenses_table
                (id, amount, description, category, dateString, timestamp, eventId, emotion)
                VALUES ('e1', 12.5, 'coffee', 'FOOD', 'Сегодня', 1000, 'event-abc', 'HAPPY')
                """.trimIndent()
            )
        }

        // 2) Проганяем миграцию 5→6 и валидируем итоговую схему.
        helper.runMigrationsAndValidate(
            TEST_DB,
            6,
            true,
            SafeSpaceDatabase.MIGRATION_5_6,
        ).use { db ->
            db.query("SELECT id, amount, eventId, recurringEventId FROM expenses_table")
                .use { c ->
                    assertEquals(true, c.moveToFirst())
                    assertEquals("e1", c.getString(0))
                    assertEquals(12.5, c.getDouble(1), 0.0)
                    assertEquals("event-abc", c.getString(2))
                    // Новая колонка nullable → у старой строки NULL.
                    assertEquals(true, c.isNull(3))
                }
        }

        // 3) Прогон через полный Room-стек: writer должен успешно вставить
        //    строку с recurringEventId, reader — прочитать. Тут же Room
        //    сверит identityHash (наш v6 хэш) — валидатор схемы.
        val roomDb = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SafeSpaceDatabase::class.java,
            TEST_DB,
        )
            .addMigrations(
                SafeSpaceDatabase.MIGRATION_4_5,
                SafeSpaceDatabase.MIGRATION_5_6,
            )
            .build()

        roomDb.openHelper.writableDatabase.execSQL(
            """
            INSERT INTO expenses_table
            (id, amount, description, category, dateString, timestamp, eventId, emotion, recurringEventId)
            VALUES ('e2', 20.0, 'gym snack', 'FOOD', 'Сегодня', 2000, 'event-xyz', NULL, 'series-42')
            """.trimIndent()
        )
        roomDb.openHelper.readableDatabase
            .query("SELECT recurringEventId FROM expenses_table WHERE id = 'e2'")
            .use { c ->
                assertEquals(true, c.moveToFirst())
                assertEquals("series-42", c.getString(0))
            }
        roomDb.openHelper.readableDatabase
            .query("SELECT recurringEventId FROM expenses_table WHERE id = 'e1'")
            .use { c ->
                assertEquals(true, c.moveToFirst())
                assertNull(c.getString(0))
            }

        roomDb.close()
    }

    @Test
    fun migrate_6_to_7_creates_pending_drafts_table() {
        // 1. Открыть БД в версии 6 с MigrationTestHelper
        val dbV6 = helper.createDatabase(TEST_DB, 6)

        // 2. Вставить legacy-строку в expenses_table (проверить что старые данные переживут)
        dbV6.execSQL("""INSERT INTO expenses_table (id, amount, description, category, dateString, timestamp, eventId, emotion, recurringEventId) 
                      VALUES ('legacy-1', 1000.0, 'Legacy expense', 'Food', '2026-08-18', 1000, null, null, null)""")
        dbV6.close()

        // 3. Реопен с миграцией 6→7
        val dbV7 = helper.runMigrationsAndValidate(TEST_DB, 7, true, MIGRATION_6_7)

        // 4. Проверить: старая строка на месте (миграция не деструктивная)
        val cursor = dbV7.query("SELECT id FROM expenses_table WHERE id = 'legacy-1'")
        assertTrue(cursor.moveToFirst())
        cursor.close()

        // 5. Проверить: pending_expense_drafts существует и пуста
        val draftCursor = dbV7.query("SELECT COUNT(*) FROM pending_expense_drafts")
        assertTrue(draftCursor.moveToFirst())
        assertEquals(0, draftCursor.getInt(0))
        draftCursor.close()
    }
}
